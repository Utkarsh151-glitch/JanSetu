package `in`.jansetu.app.catalog

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.Base64

class CatalogUpdatesTest {
    private val full = CatalogParser.parse(File("src/main/assets/catalog.json").readText())
    private val key = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    private fun packageFor(catalog: Catalog, scope: String = "local_development"): ByteArray {
        val payload = Json.encodeToString(catalog).toByteArray()
        val manifest=buildJsonObject {
            put("format",1);put("scope",scope);put("version",catalog.version);put("websites",catalog.websites.size)
            put("bytes",payload.size);put("sha256",MessageDigest.getInstance("SHA-256").digest(payload).joinToString("") {"%02x".format(it)})
            put("generatedAt","2026-10-15T00:00:00Z")
        }.toString().toByteArray()
        val signature=Signature.getInstance("SHA256withECDSA").apply { initSign(key.private);update(manifest) }.sign()
        return buildJsonObject {
            put("manifest",Base64.getEncoder().encodeToString(manifest));put("catalog",Base64.getEncoder().encodeToString(payload));put("signature",Base64.getEncoder().encodeToString(signature))
        }.toString().toByteArray()
    }
    private fun small()=full.copy(version=full.version+1,websites=listOf(full.websites.first()),guides=emptyList())

    @Test fun trustedPackageValidatesAndRollbackOrWrongKeyCannotReplaceIt() {
        val bytes=packageFor(small())
        assertEquals(full.version+1,CatalogUpdateVerifier.verify(bytes,key.public.encoded,full.version).catalog.version)
        assertTrue(runCatching {CatalogUpdateVerifier.verify(bytes,key.public.encoded,full.version+1)}.isFailure)
        val other=KeyPairGenerator.getInstance("EC").apply {initialize(256)}.generateKeyPair()
        assertTrue(runCatching {CatalogUpdateVerifier.verify(bytes,other.public.encoded,full.version)}.isFailure)
        assertTrue(runCatching {CatalogUpdateVerifier.verify(packageFor(small(),"public"),key.public.encoded,full.version)}.isFailure)
    }

    @Test fun modifiedPayloadOversizedInputAndNewBrowserPermissionsAreRejected() {
        val bytes=packageFor(small())
        val original=Json.parseToJsonElement(bytes.toString(Charsets.UTF_8)).jsonObject
        val tampered=JsonObject(original + ("catalog" to JsonPrimitive(Base64.getEncoder().encodeToString("{}".toByteArray())))).toString().toByteArray()
        assertTrue(runCatching {CatalogUpdateVerifier.verify(tampered,key.public.encoded,full.version)}.isFailure)
        assertTrue(runCatching {CatalogUpdateVerifier.readBounded(ByteArray(CatalogUpdateVerifier.MAX_PACKAGE_BYTES+1).inputStream())}.isFailure)
        val unsafe=small().copy(websites=listOf(small().websites.first().copy(id="unreviewed-browser",browserSupport="in_app")))
        assertTrue(runCatching {CatalogUpdateVerifier.verify(packageFor(unsafe),key.public.encoded,full.version)}.isFailure)
    }

    @Test fun compactRuntimePreservesRecordsSearchAndAddsLocalizedHierarchy() {
        val runtime=CatalogParser.parse(File("build/generated/runtimeAssets/catalog.json").readText())
        assertEquals(full.websites.map {it.id},runtime.websites.map {it.id})
        val queries=listOf("DigiLocker","health","National Scholarship Portal","Madhya Pradesh","file a complaint","not-a-real-portal-991299")
        // Compaction must not change search. Checked website profiles (Phase 6B/6C) intentionally add text and a
        // ranking boost, so they are removed here and tested separately in WebsiteProfilesTest.
        val compacted=runtime.copy(websiteProfiles=emptyList())
        val expected=queries.map { CatalogSearch.find(full,it).map {s->s.id} }
        val actual=queries.map { CatalogSearch.find(compacted,it).map {s->s.id} }
        assertEquals(expected,actual)
        assertEquals(4,runtime.categoryGroups.size)
        assertTrue(runtime.guides.all { it.titleHi!=null && it.stepsHi.size==it.steps.size })
        assertTrue(File("build/generated/runtimeAssets/catalog.json").length()<File("src/main/assets/catalog.json").length()/2)
    }
}
