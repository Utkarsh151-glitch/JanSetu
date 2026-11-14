# Android in-app browser policy

The National Portal of India is the only catalog record currently set to `browserSupport=in_app`. The API 37 emulator showed its home page rendering with the portal's own JavaScript and DOM storage enabled. All other listed portals open in the device browser, including DigiLocker and CPGRAMS authentication flows.

`BrowserPolicy` requires HTTPS without embedded credentials or unusual ports. Main-frame navigation remains in the WebView only on the configured portal's exact host (`www.` equivalent). Other HTTPS links hand off to the device browser; non-HTTPS and other schemes are blocked in the WebView. TLS errors are cancelled. Cleartext and mixed content are disallowed. File/content access, new windows, and JavaScript window creation are disabled. There is no JavaScript bridge or injected page code. Downloads hand off to the external browser; file-upload callbacks are cancelled with guidance to use the external browser. Authentication/payment paths on the same host also hand off externally. Android Safe Browsing is enabled.

The WebView shows its current host, loading progress, retry state, and back/forward/home/refresh controls. A user-invoked A+ control changes Android WebView's text zoom from 100% to 125%; tapping it again restores 100%. This uses `WebSettings.setTextZoom` and injects no CSS or JavaScript. Closing the WebView returns to the native companion guide. The app does not read page content or form fields. A live page can still change or break; the external-browser button is always available.

Android documents [WebSettings.setTextZoom](https://developer.android.com/reference/android/webkit/WebSettings) as a percentage-based text zoom setting. This control changes rendering on the device; it does not reduce network or server time.

Automated unit tests cover host and scheme policy. The National Portal home page was opened and visually checked in the API 37 emulator on 13 October 2026. Invalid-certificate simulation, authentication/payment handoff, downloads, redirects involving POST, lifecycle stress, and other devices remain unverified. Do not label other records as in-app compatible without individual review.
