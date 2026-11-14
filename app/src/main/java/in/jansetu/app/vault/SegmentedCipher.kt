package `in`.jansetu.app.vault

import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Seals small values (per-document keys and metadata) under the vault's master key, bound to [context].
 * The device implementation keeps that key in the Android Keystore; it never leaves secure hardware.
 */
interface VaultKeyCipher {
    fun seal(data: ByteArray, context: String): ByteArray
    fun open(data: ByteArray, context: String): ByteArray
}

/** Read access to one decrypted document. Implementations are not thread-safe. */
interface VaultReader : Closeable {
    val size: Long
    /** Reads up to [length] bytes at [offset]; returns 0 at the end. */
    fun read(offset: Long, buffer: ByteArray, bufferOffset: Int, length: Int): Int
    fun copyTo(output: OutputStream) {
        val buffer = ByteArray(SegmentedCipher.SEGMENT_SIZE)
        var offset = 0L
        while (offset < size) {
            val count = read(offset, buffer, 0, buffer.size)
            if (count <= 0) throw IOException("Unexpected end of document")
            output.write(buffer, 0, count); offset += count
        }
    }
    fun readAll(): ByteArray = ByteArrayOutputStream(size.toInt()).also { copyTo(it) }.toByteArray()
}

/**
 * Streaming AES-256-GCM in 64 KiB segments with a random key for each document.
 *
 * Layout: magic "JSVD", version, segment size, 7-byte nonce prefix, then the per-document key sealed by
 * [VaultKeyCipher] for "dek:<id>". Each segment is sealed with nonce = prefix || counter || last-flag and the
 * header plus document ID as associated data. Reordering, truncation, a swapped file or a changed header all
 * fail authentication. Plaintext never touches storage: import streams through it, reading decrypts segments
 * on demand.
 */
object SegmentedCipher {
    const val SEGMENT_SIZE = 64 * 1024
    private const val TAG_BYTES = 16
    private const val PREFIX_BYTES = 7
    private val MAGIC = "JSVD".toByteArray(Charsets.US_ASCII)
    private const val VERSION: Byte = 1
    private const val FIXED_HEADER = 4 + 1 + 4 + PREFIX_BYTES + 2
    private const val INSPECT = 1024
    private val random = SecureRandom()

    class Result(val plaintextBytes: Long, val sha256: String, val head: ByteArray, val tail: ByteArray)

    fun encrypt(documentId: String, input: InputStream, output: OutputStream, keys: VaultKeyCipher, maxBytes: Long): Result {
        val dataKey = ByteArray(32).also(random::nextBytes)
        try {
            val prefix = ByteArray(PREFIX_BYTES).also(random::nextBytes)
            val sealed = keys.seal(dataKey, "dek:$documentId")
            require(sealed.size in 1..1024)
            val header = ByteBuffer.allocate(FIXED_HEADER + sealed.size).put(MAGIC).put(VERSION).putInt(SEGMENT_SIZE)
                .put(prefix).putShort(sealed.size.toShort()).put(sealed).array()
            output.write(header)
            val aad = header + documentId.toByteArray()
            val key = SecretKeySpec(dataKey, "AES")
            val digest = MessageDigest.getInstance("SHA-256")
            var current = readSegment(input)
            if (current.isEmpty()) throw VaultImportException(ImportProblem.EMPTY)
            val head = current.copyOf(minOf(INSPECT, current.size))
            var tail = ByteArray(0)
            var total = 0L; var counter = 0
            while (true) {
                val next = if (current.size == SEGMENT_SIZE) readSegment(input) else ByteArray(0)
                val last = next.isEmpty()
                total += current.size
                if (total > maxBytes) throw VaultImportException(ImportProblem.TOO_LARGE)
                digest.update(current)
                tail = (tail + current).let { it.copyOfRange(maxOf(0, it.size - INSPECT), it.size) }
                output.write(cipher(Cipher.ENCRYPT_MODE, key, prefix, counter, last, aad).doFinal(current))
                current.fill(0)
                counter++
                if (last) break
                current = next
            }
            return Result(total, digest.digest().joinToString("") { "%02x".format(it) }, head, tail)
        } finally { dataKey.fill(0) }
    }

    /** Opens an encrypted document for random-access reading. The caller closes the reader. */
    fun open(documentId: String, file: RandomAccessFile, keys: VaultKeyCipher): VaultReader {
        try {
            val fixed = ByteArray(FIXED_HEADER).also { file.seek(0); file.readFully(it) }
            val buffer = ByteBuffer.wrap(fixed)
            val magic = ByteArray(4).also { buffer.get(it) }
            if (!magic.contentEquals(MAGIC) || buffer.get() != VERSION || buffer.int != SEGMENT_SIZE) throw IOException("Not a vault document")
            val prefix = ByteArray(PREFIX_BYTES).also { buffer.get(it) }
            val sealedLength = buffer.short.toInt() and 0xFFFF
            if (sealedLength !in 1..1024) throw IOException("Invalid header")
            val sealed = ByteArray(sealedLength).also { file.readFully(it) }
            val header = fixed + sealed
            val body = file.length() - header.size
            val segmentBytes = SEGMENT_SIZE + TAG_BYTES
            if (body <= TAG_BYTES) throw IOException("Truncated document")
            val segments = ((body + segmentBytes - 1) / segmentBytes).toInt()
            val lastPlain = (body - (segments - 1).toLong() * segmentBytes - TAG_BYTES).toInt()
            if (lastPlain !in 1..SEGMENT_SIZE) throw IOException("Truncated document")
            val dataKey = keys.open(sealed, "dek:$documentId")
            return SegmentReader(file, header.size.toLong(), segments, lastPlain, prefix, header + documentId.toByteArray(), dataKey)
        } catch (error: Exception) {
            file.close()
            throw if (error is IOException) error else IOException("Document could not be opened", error)
        }
    }

    private class SegmentReader(
        private val file: RandomAccessFile, private val bodyStart: Long, private val segments: Int,
        private val lastPlain: Int, private val prefix: ByteArray, private val aad: ByteArray, dataKey: ByteArray,
    ) : VaultReader {
        private val key = SecretKeySpec(dataKey, "AES").also { dataKey.fill(0) }
        override val size: Long = (segments - 1).toLong() * SEGMENT_SIZE + lastPlain
        private var cachedIndex = -1
        private var cached = ByteArray(0)
        private var closed = false

        private fun segment(index: Int): ByteArray {
            check(!closed)
            if (index == cachedIndex) return cached
            val last = index == segments - 1
            val length = (if (last) lastPlain else SEGMENT_SIZE) + TAG_BYTES
            val sealed = ByteArray(length)
            file.seek(bodyStart + index.toLong() * (SEGMENT_SIZE + TAG_BYTES)); file.readFully(sealed)
            val plain = try { cipher(Cipher.DECRYPT_MODE, key, prefix, index, last, aad).doFinal(sealed) }
                catch (error: Exception) { throw IOException("Document failed its integrity check", error) }
            cached.fill(0); cached = plain; cachedIndex = index
            return plain
        }

        override fun read(offset: Long, buffer: ByteArray, bufferOffset: Int, length: Int): Int {
            if (offset < 0 || length < 0 || bufferOffset < 0 || bufferOffset + length > buffer.size) throw IOException("Invalid read")
            var copied = 0
            var position = offset
            while (copied < length && position < size) {
                val index = (position / SEGMENT_SIZE).toInt()
                val plain = segment(index)
                val within = (position - index.toLong() * SEGMENT_SIZE).toInt()
                val count = minOf(length - copied, plain.size - within)
                System.arraycopy(plain, within, buffer, bufferOffset + copied, count)
                copied += count; position += count
            }
            return copied
        }

        override fun close() {
            if (closed) return
            closed = true; cached.fill(0); file.close()
        }
    }

    private fun cipher(mode: Int, key: SecretKeySpec, prefix: ByteArray, counter: Int, last: Boolean, aad: ByteArray): Cipher {
        val nonce = ByteBuffer.allocate(12).put(prefix).putInt(counter).put(if (last) 1 else 0).array()
        return Cipher.getInstance("AES/GCM/NoPadding").apply { init(mode, key, GCMParameterSpec(TAG_BYTES * 8, nonce)); updateAAD(aad) }
    }

    private fun readSegment(input: InputStream): ByteArray {
        val buffer = ByteArray(SEGMENT_SIZE)
        var filled = 0
        while (filled < SEGMENT_SIZE) {
            val count = input.read(buffer, filled, SEGMENT_SIZE - filled)
            if (count < 0) break
            filled += count
        }
        return if (filled == SEGMENT_SIZE) buffer else buffer.copyOf(filled).also { buffer.fill(0) }
    }
}
