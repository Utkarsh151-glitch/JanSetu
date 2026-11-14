package `in`.jansetu.app.vault

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.random.Random

/** Software stand-in for the Keystore key, using the same sealing format. */
class SoftwareKeys(private val key: SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()) : VaultKeyCipher {
    override fun seal(data: ByteArray, context: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key); updateAAD(context.toByteArray()) }
        return byteArrayOf(1) + cipher.iv + cipher.doFinal(data)
    }
    override fun open(data: ByteArray, context: String): ByteArray = Cipher.getInstance("AES/GCM/NoPadding").run {
        init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, data.copyOfRange(1, 13))); updateAAD(context.toByteArray()); doFinal(data.copyOfRange(13, data.size))
    }
}

class SegmentedCipherTest {
    @get:Rule val folder = TemporaryFolder()
    private val keys = SoftwareKeys()
    private val segment = SegmentedCipher.SEGMENT_SIZE

    private fun encrypt(data: ByteArray, id: String = UUID.randomUUID().toString(), max: Long = VaultLimits.MAX_FILE_BYTES): Pair<File, SegmentedCipher.Result> {
        val file = folder.newFile()
        val result = file.outputStream().use { SegmentedCipher.encrypt(id, ByteArrayInputStream(data), it, keys, max) }
        return file to result
    }

    private fun sha(data: ByteArray) = MessageDigest.getInstance("SHA-256").digest(data).joinToString("") { "%02x".format(it) }

    @Test fun roundTripsEverySizeIncludingExactSegmentBoundaries() {
        for (size in listOf(1, 17, segment - 1, segment, segment + 1, 3 * segment, 3 * segment + 5)) {
            val data = Random(size).nextBytes(size)
            val id = UUID.randomUUID().toString()
            val (file, result) = encrypt(data, id)
            assertEquals(size.toLong(), result.plaintextBytes)
            assertEquals(sha(data), result.sha256)
            assertArrayEquals(data.copyOf(minOf(1024, size)), result.head)
            assertArrayEquals(data.copyOfRange(maxOf(0, size - 1024), size), result.tail)
            SegmentedCipher.open(id, RandomAccessFile(file, "r"), keys).use { reader ->
                assertEquals(size.toLong(), reader.size)
                assertArrayEquals(data, reader.readAll())
            }
        }
    }

    @Test fun randomAccessReadsMatchThePlaintext() {
        val data = Random(7).nextBytes(5 * segment + 123)
        val id = UUID.randomUUID().toString()
        val (file, _) = encrypt(data, id)
        SegmentedCipher.open(id, RandomAccessFile(file, "r"), keys).use { reader ->
            for ((offset, length) in listOf(0L to 10, segment - 3L to 9, 2L * segment + 500 to 3 * segment, data.size - 4L to 100, data.size.toLong() to 10)) {
                val buffer = ByteArray(length)
                val count = reader.read(offset, buffer, 0, length)
                val expected = data.copyOfRange(minOf(offset.toInt(), data.size), minOf(data.size, offset.toInt() + length))
                assertEquals(expected.size, count)
                assertArrayEquals(expected, buffer.copyOf(count))
            }
        }
    }

    @Test fun ciphertextNeverContainsThePlaintext() {
        val marker = "JANSETU-SYNTHETIC-PLAINTEXT-MARKER-0001".toByteArray()
        val data = ByteArray(0).let { var d = it; repeat(4000) { d += marker }; d }
        val (file, _) = encrypt(data)
        val stored = file.readBytes()
        assertEquals(-1, DocumentSignature.indexOf(stored, marker))
        assertTrue(stored.size > data.size)
    }

    @Test fun tamperingTruncationReorderingAndSwappingAreDetected() {
        val data = Random(3).nextBytes(3 * segment + 10)
        val id = UUID.randomUUID().toString()
        val (file, _) = encrypt(data, id)
        val original = file.readBytes()
        fun fails(bytes: ByteArray, openId: String = id, keyCipher: VaultKeyCipher = keys) {
            val copy = folder.newFile().apply { writeBytes(bytes) }
            val result = runCatching { SegmentedCipher.open(openId, RandomAccessFile(copy, "r"), keyCipher).use { it.readAll() } }
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IOException)
        }
        fails(original.copyOf().also { it[it.size - 100] = (it[it.size - 100].toInt() xor 1).toByte() })
        fails(original.copyOf().also { it[10] = (it[10].toInt() xor 1).toByte() })
        // Dropping the last segment leaves a non-final segment at the end.
        val headerLength = original.size - (3 * (segment + 16) + 10 + 16)
        fails(original.copyOf(headerLength + 3 * (segment + 16)))
        fails(original.copyOf(original.size - 1))
        // Swapping two segments.
        val swapped = original.copyOf()
        System.arraycopy(original, headerLength + segment + 16, swapped, headerLength, segment + 16)
        System.arraycopy(original, headerLength, swapped, headerLength + segment + 16, segment + 16)
        fails(swapped)
        // A file presented under another document's ID, or opened with another key.
        fails(original, openId = UUID.randomUUID().toString())
        fails(original, keyCipher = SoftwareKeys())
        fails(ByteArray(10))
    }

    @Test fun emptyAndOversizedInputsAreRefused() {
        val empty = runCatching { encrypt(ByteArray(0)) }.exceptionOrNull()
        assertEquals(ImportProblem.EMPTY, (empty as VaultImportException).problem)
        val large = runCatching { encrypt(ByteArray(2 * segment + 1), max = 2L * segment) }.exceptionOrNull()
        assertEquals(ImportProblem.TOO_LARGE, (large as VaultImportException).problem)
        assertNotNull(encrypt(ByteArray(2 * segment), max = 2L * segment).second)
    }

    @Test fun eachDocumentGetsItsOwnKeyAndNonces() {
        val data = Random(1).nextBytes(1000)
        val a = encrypt(data).first.readBytes(); val b = encrypt(data).first.readBytes()
        assertFalse(a.contentEquals(b))
        assertEquals(a.size, b.size)
    }

    @Test fun closedReadersRefuseFurtherReads() {
        val id = UUID.randomUUID().toString()
        val (file, _) = encrypt(Random(2).nextBytes(100), id)
        val reader = SegmentedCipher.open(id, RandomAccessFile(file, "r"), keys)
        reader.close(); reader.close()
        assertTrue(runCatching { reader.read(0, ByteArray(10), 0, 10) }.isFailure)
    }
}
