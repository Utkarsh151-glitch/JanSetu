package `in`.jansetu.app.browser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrowserPolicyTest {
    private val official = "https://www.india.gov.in/"

    @Test fun onlyTheConfiguredHttpsHostStaysInApp() {
        assertTrue(BrowserPolicy.staysInApp("https://india.gov.in/services", official))
        assertTrue(BrowserPolicy.staysInApp("https://www.india.gov.in/", official))
        assertFalse(BrowserPolicy.staysInApp("http://www.india.gov.in/", official))
        assertFalse(BrowserPolicy.staysInApp("https://www.india.gov.in.evil.example/", official))
        assertFalse(BrowserPolicy.staysInApp("https://accounts.india.gov.in/", official))
        assertFalse(BrowserPolicy.staysInApp("javascript:alert(1)", official))
        assertFalse(BrowserPolicy.isSafeHttps("https://user:password@india.gov.in/"))
        assertFalse(BrowserPolicy.isSafeHttps("https://india.gov.in:8080/"))
    }
    @Test fun authenticationAndPaymentJourneysUseExternalBrowserEvenOnTheSameHost() {
        for (path in listOf("/login","/user/register","/payment/checkout","/auth/callback","/%6cogin")) {
            assertFalse(BrowserPolicy.staysInApp("https://www.india.gov.in$path",official))
        }
        assertTrue(BrowserPolicy.staysInApp("https://www.india.gov.in/services",official))
    }
    @Test fun sensitiveQueryFragmentAndNestedEncodingCannotStayInTheReadOnlyBrowser() {
        listOf("/?action=login","/#/signin","/services?next=/payment/checkout","/%256cogin","/?mode=oauth2","/application/status","/upload").forEach {
            assertFalse(it,BrowserPolicy.staysInApp("https://www.india.gov.in$it",official))
        }
        assertTrue(BrowserPolicy.staysInApp("https://www.india.gov.in/services?page=2",official))
    }
    // Phase 6A: the in-app reading context covers only the reviewed host's public pages.
    @Test fun inAppNavigationStaysOnTheExactReviewedHostAndHandsEverythingElseOff() {
        listOf("https://www.india.gov.in/#main","https://www.india.gov.in/topics/education?page=3","https://india.gov.in/my-government").forEach {
            assertTrue(it,BrowserPolicy.staysInApp(it,official))
        }
        listOf("https://www.india.gov.in./","https://www.xn--india-gov-in.example/","https://services.india.gov.in/",
            "https://www.india.gov.in.example/","https://rtionline.gov.in/guidelines.php?request","https://www.india.gov.in/user/signup",
            "https://www.india.gov.in/?redirect=%2Flogin","ftp://www.india.gov.in/","https://www.india.gov.in:444/").forEach {
            assertFalse(it,BrowserPolicy.staysInApp(it,official))
        }
    }
    @Test fun encodedControlsAndBackslashesAreNotSafeHandoffs() {
        listOf("https://www.india.gov.in/%0aheader","https://www.india.gov.in/%0D%0Aheader","https://www.india.gov.in/%5cpath").forEach {
            assertFalse(it,BrowserPolicy.isSafeHttps(it))
        }
        assertTrue(BrowserPolicy.isSafeHttps("https://www.india.gov.in/a%20document.pdf"))
    }
}
