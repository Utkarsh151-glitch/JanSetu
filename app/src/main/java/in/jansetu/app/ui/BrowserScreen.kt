package `in`.jansetu.app.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceResponse
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.ValueCallback
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import `in`.jansetu.app.browser.BrowserPolicy
import `in`.jansetu.app.catalog.Website
import `in`.jansetu.app.settings.LocalSettings
import java.net.URI
import kotlin.math.roundToInt
import android.content.Context
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Website text size in JanSetu's in-app browser. It starts from the person's Android font size, so large system
 * text also enlarges the official page; A returns to the site's own size and A+ adds 25 %, up to 200 %. Only the
 * WebView's text zoom changes: the page's code, layout rules and controls are not modified.
 */
object BrowserTextSize {
    const val MIN = 100
    const val MAX = 200
    fun initial(fontScale: Float): Int = (fontScale * 100).roundToInt().coerceIn(MIN, MAX)
    fun larger(current: Int): Int = (current + 25).coerceAtMost(MAX)
    /** Reading position as a share of the scrollable height, so it survives reflow after a rotation or font change. */
    fun ratio(scrollY: Int, range: Int): Float = if (range <= 0) 0f else (scrollY.toFloat() / range).coerceIn(0f, 1f)
    fun offset(ratio: Float, range: Int): Int = if (range <= 0) 0 else (ratio.coerceIn(0f, 1f) * range).roundToInt()
}

/** Exposes the vertical scroll range, which WebView keeps protected. */
private class ReadingWebView(context: Context) : WebView(context) {
    fun scrollRange(): Int = computeVerticalScrollRange() - height
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(site: Website, settings: LocalSettings, onClose: () -> Unit, onExternal: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var progress by remember { mutableIntStateOf(0) }
    var currentUrl by rememberSaveable(site.id) { mutableStateOf(site.url) }
    var error by remember { mutableStateOf<String?>(null) }
    var historyVersion by remember { mutableIntStateOf(0) }
    // null follows the Android font size; a number is the person's own A or A+ choice.
    var chosenZoom by rememberSaveable(site.id) { mutableStateOf<Int?>(null) }
    val textZoom = chosenZoom ?: BrowserTextSize.initial(LocalConfiguration.current.fontScale)
    // Saved across rotation and font-size changes (which recreate the screen) so the reader returns to the same place.
    var scrollRatio by rememberSaveable(site.id) { mutableFloatStateOf(0f) }
    var restorePending by remember(site.id) { mutableStateOf(scrollRatio > 0f) }
    // Returns to the saved reading position once the reloaded page has a scrollable height; gives up after about 3 s.
    fun restoreScroll(view: ReadingWebView, attempt: Int) {
        val range = view.scrollRange()
        if (range > 0 || attempt >= 12) {
            view.scrollTo(0, BrowserTextSize.offset(scrollRatio, range))
            restorePending = false
        } else view.postDelayed({ restoreScroll(view, attempt + 1) }, 250)
    }
    val webView = remember(site.id) {
        ReadingWebView(context).apply {
            this.settings.javaScriptEnabled = site.browserJavaScript
            this.settings.domStorageEnabled = site.browserJavaScript
            this.settings.allowFileAccess = false
            this.settings.allowContentAccess = false
            this.settings.javaScriptCanOpenWindowsAutomatically = false
            this.settings.setSupportMultipleWindows(false)
            this.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            this.settings.safeBrowsingEnabled = true
            this.settings.textZoom = textZoom
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) { progress = newProgress }
                override fun onShowFileChooser(view: WebView?, callback: ValueCallback<Array<Uri>>, params: FileChooserParams): Boolean {
                    callback.onReceiveValue(null)
                    error = t(settings,"File uploads must be opened in your device browser.","फ़ाइल अपलोड अपने डिवाइस के ब्राउज़र में करें।")
                    return true
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                    if (!request.isForMainFrame) return false
                    val url = request.url.toString()
                    if (BrowserPolicy.staysInApp(url, site.url)) return false
                    if (BrowserPolicy.isSafeHttps(url)) onExternal(url)
                    else error = t(settings,"This link type cannot open inside JanSetu.","यह लिंक जनसेतु के अंदर नहीं खुल सकता।")
                    return true
                }
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    if (url != null && !BrowserPolicy.staysInApp(url, site.url)) {
                        view?.stopLoading()
                        if (BrowserPolicy.isSafeHttps(url)) onExternal(url)
                        else error = t(settings,"This link type cannot open inside JanSetu.","यह लिंक जनसेतु के अंदर नहीं खुल सकता।")
                        return
                    }
                    error = null
                    currentUrl = url ?: site.url
                    historyVersion++
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    historyVersion++
                    if (restorePending && view is ReadingWebView) restoreScroll(view, 0)
                }
                override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest, response: WebResourceResponse) {
                    if (request.isForMainFrame && response.statusCode >= 400) {
                        error = t(settings,"This government page returned an error. Try again later or open it in your browser.","यह सरकारी पृष्ठ त्रुटि दे रहा है। बाद में फिर कोशिश करें या इसे अपने ब्राउज़र में खोलें।")
                    }
                }
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, sslError: SslError) {
                    handler.cancel()
                    error = t(settings,"Secure connection failed. Open the official site in your browser or try again later.","सुरक्षित कनेक्शन असफल हुआ। आधिकारिक साइट अपने ब्राउज़र में खोलें या बाद में प्रयास करें।")
                }
                override fun onReceivedError(view: WebView?, request: WebResourceRequest, resourceError: WebResourceError) {
                    if (request.isForMainFrame) error = t(settings,"This page could not load. Check your connection or open it in your browser.","यह पेज लोड नहीं हुआ। कनेक्शन जाँचें या अपने ब्राउज़र में खोलें।")
                }
            }
            setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (!restorePending) scrollRatio = BrowserTextSize.ratio(scrollY, (this as ReadingWebView).scrollRange())
            }
            setDownloadListener { url, _, _, _, _ ->
                if (BrowserPolicy.isSafeHttps(url)) onExternal(url)
            }
            loadUrl(if (BrowserPolicy.staysInApp(currentUrl, site.url)) currentUrl else site.url)
        }
    }
    LaunchedEffect(webView, textZoom) { webView.settings.textZoom = textZoom }
    DisposableEffect(webView) { onDispose { webView.stopLoading(); webView.destroy() } }
    DisposableEffect(webView, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if(event==Lifecycle.Event.ON_PAUSE) webView.onPause()
            if(event==Lifecycle.Event.ON_RESUME) webView.onResume()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    BackHandler { if (webView.canGoBack()) webView.goBack() else onClose() }
    @Suppress("UNUSED_VARIABLE") val refreshHistory = historyVersion

    Column(Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column {
                Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = t(settings, "Close browser", "ब्राउज़र बंद करें")) }
                    Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                        Text(site.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(4.dp))
                            Text(runCatching { URI(currentUrl).host }.getOrNull() ?: site.domain,
                                style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    TextButton(onClick = { chosenZoom = BrowserTextSize.MIN }, enabled = textZoom != BrowserTextSize.MIN,
                        modifier = Modifier.testTag("browser-text-normal").semantics {
                            contentDescription = t(settings, "Normal website text", "वेबसाइट का सामान्य पाठ")
                        }) { Text("A") }
                    Text("$textZoom%", style = MaterialTheme.typography.labelMedium, modifier = Modifier.testTag("browser-text-size").semantics {
                        contentDescription = t(settings, "Website text size $textZoom percent", "वेबसाइट पाठ का आकार $textZoom प्रतिशत")
                    })
                    TextButton(onClick = { chosenZoom = BrowserTextSize.larger(textZoom) }, enabled = textZoom < BrowserTextSize.MAX,
                        modifier = Modifier.testTag("browser-text-larger").semantics {
                            contentDescription = t(settings, "Larger website text", "वेबसाइट का बड़ा पाठ")
                        }) { Text("A+") }
                    IconButton(onClick = { onExternal(currentUrl) }) { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = t(settings, "Open in external browser", "बाहरी ब्राउज़र में खोलें")) }
                }
                // Keeps JanSetu's frame visibly separate from the government page inside it.
                Text(t(settings, "Official website in JanSetu's limited, read-only browser. JanSetu does not control this page.",
                    "जनसेतु के सीमित, केवल-पढ़ने वाले ब्राउज़र में आधिकारिक वेबसाइट। इस पेज पर जनसेतु का नियंत्रण नहीं है।"),
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))
            }
        }
        if (progress < 100) LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth())
        if (error != null) {
            JanSetuNotice(error!!, Modifier.padding(16.dp), tone = StatusTone.ERROR, title = t(settings, "Page not shown", "पेज नहीं दिखाया गया")) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { error = null; webView.reload() }) { Text(t(settings, "Retry", "फिर कोशिश करें")) }
                    TextButton(onClick = { onExternal(currentUrl) }) { Text(t(settings, "Open in your browser", "अपने ब्राउज़र में खोलें")) }
                }
            }
        }
        AndroidView(factory = { webView }, modifier = Modifier.weight(1f).fillMaxWidth().testTag("browser-webview"))
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { webView.goBack() }, enabled = webView.canGoBack()) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t(settings, "Back", "पीछे")) }
                IconButton(onClick = { webView.goForward() }, enabled = webView.canGoForward()) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = t(settings, "Forward", "आगे")) }
                IconButton(onClick = { webView.loadUrl(site.url) }) { Icon(Icons.Default.Home, contentDescription = t(settings, "Portal home", "पोर्टल होम")) }
                IconButton(onClick = { webView.reload() }) { Icon(Icons.Default.Refresh, contentDescription = t(settings, "Refresh", "फिर लोड करें")) }
            }
        }
    }
}