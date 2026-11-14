package `in`.jansetu.app

import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.*
import `in`.jansetu.app.tracker.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.util.Base64
import java.util.UUID
import java.time.LocalDate

class TrackerCatalogBoundaryTest {
    @Test fun actualSignedInstallAndBundleRestoreDoNotModifySavedApplicationHistory()=runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val unique=UUID.randomUUID().toString();val alias="tracker-boundary-$unique"
        val file=File(context.noBackupFilesDir,"tracker-boundary-$unique.db")
        val directory=File(context.cacheDir,"catalog-boundary-$unique")
        val repo=SqliteApplicationRepository(context,file,KeystoreTrackerCipher(alias))
        val original=CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use {it.readText()})
        val pair=KeyPairGenerator.getInstance("EC").apply {initialize(ECGenParameterSpec("secp256r1"))}.generateKeyPair()
        val catalogs=LocalCatalogRepository(context,directory,pair.public.encoded)
        try {
            val service=original.services.first {it.hasConfirmedApplicationRoute()}
            val initial=repo.save(ApplicationDraft("Tracked before update",referenceNumber="BOUNDARY-TEST-ONLY",deadline=LocalDate.now().plusDays(10).toString(),
                reminderEnabled=true,reminderDaysBefore=3,progressNote="Retain this history"),service=service.trackerSnapshot(original.version))
            val r=repo.save(ApplicationDraft("Tracked before update",referenceNumber=initial.referenceNumber,deadline=requireNotNull(initial.deadline),
                reminderEnabled=true,reminderDaysBefore=3,status=ApplicationStatus.SUBMITTED,submittedOn=LocalDate.now().toString(),progressNote="Second history event"),id=initial.id,revision=initial.revision)
            assertEquals(2,r.history.size);assertTrue(r.reminderEnabled)
            val updated=original.copy(version=original.version+1,services=emptyList(),serviceOrganizations=emptyList())
            val payload=Json.encodeToString(updated).toByteArray()
            val manifest=buildJsonObject {
                put("format",1);put("scope","local_development");put("version",updated.version)
                put("websites",updated.websites.size);put("bytes",payload.size)
                put("sha256",MessageDigest.getInstance("SHA-256").digest(payload).joinToString("") {"%02x".format(it)})
                put("generatedAt","2026-10-16T00:00:00Z")
            }.toString().toByteArray()
            val signature=Signature.getInstance("SHA256withECDSA").apply {initSign(pair.private);update(manifest)}.sign()
            val envelope=buildJsonObject {
                put("manifest",Base64.getEncoder().encodeToString(manifest));put("catalog",Base64.getEncoder().encodeToString(payload));put("signature",Base64.getEncoder().encodeToString(signature))
            }.toString().toByteArray()
            assertTrue(catalogs.install(envelope.inputStream(),original.version).services.isEmpty())
            assertEquals(r,repo.get(r.id));assertEquals(service.id,repo.get(r.id)?.service?.serviceId)
            catalogs.restoreBundled();assertEquals(original.services.size,catalogs.load().services.size);assertEquals(r,repo.get(r.id))
            assertEquals(ApplicationRules.reminderKey(r),ApplicationRules.reminderKey(requireNotNull(repo.get(r.id))))
        } finally {
            repo.close();SQLiteDatabase.deleteDatabase(file)
            KeyStore.getInstance("AndroidKeyStore").apply {load(null);deleteEntry(alias)}
            directory.listFiles()?.forEach {check(it.isFile && it.delete())};directory.delete()
        }
    }
}
