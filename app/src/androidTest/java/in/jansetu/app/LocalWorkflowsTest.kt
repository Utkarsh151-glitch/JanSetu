package `in`.jansetu.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import `in`.jansetu.app.catalog.*
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.Base64

class LocalWorkflowsTest {
    @get:Rule val rule=createComposeRule(effectContext=StandardTestDispatcher())
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    private fun catalog()=CatalogParser.parse(context.assets.open("catalog.json").bufferedReader().use {it.readText()})
    private fun settings():LocalSettings {
        context.getSharedPreferences("jansetu_preferences",0).edit().clear().commit()
        return LocalSettings(context).apply {finishOnboarding()}
    }

    @Test fun favoriteRecentSearchAndHistoryWorkThroughTheApp() {
        val settings=settings()
        rule.setContent {JanSetuTheme {JanSetuApp(settings)}}
        rule.waitUntil(20_000) {rule.onAllNodesWithText("What government service do you need?").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Search").performClick()
        rule.onNodeWithText("What government service do you need?").performTextInput("DigiLocker")
        rule.waitUntil(20_000) {rule.onAllNodesWithText("www.digilocker.gov.in",substring=true).fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("www.digilocker.gov.in",substring=true).performClick()
        rule.onNodeWithText("Save to favorites").performScrollTo().performClick()
        rule.runOnIdle {assertTrue("digilocker" in settings.favorites);assertTrue("digilocker" in settings.recent);assertEquals("DigiLocker",settings.recentSearches.first())}
        rule.onNodeWithContentDescription("Back").performScrollTo().performClick()
        rule.onNodeWithText("Saved").performClick()
        rule.onNodeWithText("DigiLocker").assertExists()
        rule.onNodeWithText("Recently viewed").performClick()
        rule.onNodeWithText("DigiLocker").assertExists()
        rule.runOnIdle {settings.clearHistory();assertTrue(settings.recent.isEmpty());assertTrue(settings.recentSearches.isEmpty());assertTrue("digilocker" in LocalSettings(context).favorites)}
        rule.onNodeWithText("No recent portals").assertExists()
    }

    @Test fun hindiGuideAndAccessibleNavigationUseRuntimeTranslations() {
        val catalog=catalog();val settings=settings().apply {updateLanguage("hi")}
        rule.setContent {JanSetuTheme {DetailScreen(catalog.websites.first {it.id=="digilocker"},catalog.guides.first {it.websiteId=="digilocker"},settings,{},{},{},{},{})}}
        rule.onNodeWithContentDescription("पीछे").assertExists()
        rule.onNodeWithText("जारी किया गया दस्तावेज़ खोजें").performScrollTo().assertExists()
        rule.onNodeWithText("यह जनसेतु की हिन्दी व्याख्या है, आधिकारिक अनुवाद नहीं।").performScrollTo().assertExists()
        assertEquals("hi",LocalSettings(context).language)
    }

    @Test fun signedUpdatePersistsOfflineRejectsDamageAndRestoresBundle()=runBlocking {
        val original=catalog()
        val pair=KeyPairGenerator.getInstance("EC").apply {initialize(ECGenParameterSpec("secp256r1"))}.generateKeyPair()
        val directory=File(context.cacheDir,"catalog-update-test-${System.nanoTime()}")
        val repo=LocalCatalogRepository(context,directory,pair.public.encoded)
        val updated=original.copy(version=original.version+1)
        val payload=Json.encodeToString(updated).toByteArray()
        val manifest=buildJsonObject {
            put("format",1);put("scope","local_development");put("version",updated.version);put("websites",updated.websites.size);put("bytes",payload.size)
            put("sha256",MessageDigest.getInstance("SHA-256").digest(payload).joinToString(""){"%02x".format(it)});put("generatedAt","2026-10-15T00:00:00Z")
        }.toString().toByteArray()
        val signature=Signature.getInstance("SHA256withECDSA").apply {initSign(pair.private);update(manifest)}.sign()
        val envelope=buildJsonObject {
            put("manifest",Base64.getEncoder().encodeToString(manifest));put("catalog",Base64.getEncoder().encodeToString(payload));put("signature",Base64.getEncoder().encodeToString(signature))
        }.toString().toByteArray()
        try {
            assertEquals(updated.version,repo.install(envelope.inputStream(),original.version).version)
            val reopened=LocalCatalogRepository(context,directory,pair.public.encoded)
            assertEquals(updated.version,reopened.load().version);assertEquals("local_update",reopened.source)
            assertTrue(runCatching {repo.install("broken".byteInputStream(),updated.version)}.isFailure)
            assertEquals(updated.version,reopened.load().version)
            repo.restoreBundled();assertEquals(original.version,repo.load().version)
            File(directory,"catalog-update.jansetu").writeText("broken")
            assertEquals(original.version,repo.load().version);assertTrue(repo.recoveredInvalidUpdate)
            repo.restoreBundled()
        } finally {
            directory.listFiles()?.filter {it.isFile}?.forEach {it.delete()};directory.delete()
        }
    }

    @Test fun organizationGroupingAndCategoryHierarchyAreNavigable() {
        val catalog=catalog();val settings=settings()
        var opened:String?=null
        rule.setContent {JanSetuTheme {OrganizationsScreen(catalog,settings,{}, {opened=it})}}
        val site=catalog.websites.first {it.id=="digilocker"}
        rule.onNodeWithText("Find an organization").performTextInput(site.organization)
        rule.onNodeWithTag("organization:${site.organization}").performClick()
        rule.onNodeWithText(site.title).performClick()
        rule.runOnIdle {assertEquals(site.id,opened)}
        assertEquals(4,catalog.categoryGroups.size)
    }
}
