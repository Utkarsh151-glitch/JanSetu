package `in`.jansetu.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.core.view.WindowCompat
import `in`.jansetu.app.catalog.LocalCatalogRepository
import `in`.jansetu.app.catalog.Catalog
import `in`.jansetu.app.catalog.profileFor
import `in`.jansetu.app.catalog.servicesFor
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.ui.CategoriesScreen
import `in`.jansetu.app.ui.BrowserScreen
import `in`.jansetu.app.ui.DetailScreen
import `in`.jansetu.app.ui.HelpScreen
import `in`.jansetu.app.ui.HomeScreen
import `in`.jansetu.app.ui.JanSetuTheme
import `in`.jansetu.app.ui.LoadingScreen
import `in`.jansetu.app.ui.OnboardingScreen
import `in`.jansetu.app.ui.PrivacyScreen
import `in`.jansetu.app.ui.SavedScreen
import `in`.jansetu.app.ui.SearchScreen
import `in`.jansetu.app.ui.SettingsScreen
import `in`.jansetu.app.ui.CatalogManagementScreen
import `in`.jansetu.app.ui.OrganizationsScreen
import `in`.jansetu.app.ui.ServiceUnavailableForTracking
import `in`.jansetu.app.ui.t
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import `in`.jansetu.app.tracker.TrackerDependencies
import `in`.jansetu.app.tracker.hasConfirmedApplicationRoute
import `in`.jansetu.app.tracker.trackerSnapshot
import `in`.jansetu.app.ui.MyApplicationsScreen
import `in`.jansetu.app.ui.ApplicationEditorScreen
import `in`.jansetu.app.ui.ApplicationDetailScreen
import `in`.jansetu.app.ui.DocumentDetailScreen
import `in`.jansetu.app.ui.MyDocumentsScreen
import `in`.jansetu.app.ui.PrepareDocumentsScreen
import `in`.jansetu.app.vault.VaultDependencies
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var requestedApplication by mutableStateOf<String?>(null)
    private fun readApplicationIntent(intent: Intent?) {
        val uri=intent?.data
        requestedApplication=uri?.takeIf {it.scheme=="jansetu-local" && it.host=="application"}?.lastPathSegment
            ?.takeIf {runCatching {UUID.fromString(it)}.isSuccess}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 15+ already draws edge to edge for target SDK 36; this keeps
        // older versions consistent so the in-app theme controls bar contrast.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        readApplicationIntent(intent)
        val settings = LocalSettings(applicationContext)
        setContent { JanSetuApp(settings,requestedApplication) }
    }
    override fun onNewIntent(intent: Intent) {super.onNewIntent(intent);setIntent(intent);readApplicationIntent(intent)}
}

private sealed interface CatalogState {
    data object Loading : CatalogState
    data class Ready(val catalog: Catalog, val source: String, val recovered: Boolean) : CatalogState
    data class Failed(val message: String) : CatalogState
}

@Composable
internal fun JanSetuApp(settings: LocalSettings, requestedApplication: String? = null,
    applicationTracker: `in`.jansetu.app.tracker.ApplicationTracker? = null, documentVault: VaultDependencies? = null) {
    // The saved appearance, motion and text choices apply to every screen,
    // including when a caller (such as a UI test) wraps the app in a theme.
    JanSetuTheme(settings) { JanSetuAppContent(settings, requestedApplication, applicationTracker, documentVault) }
}

@Composable
private fun JanSetuAppContent(settings: LocalSettings, requestedApplication: String?,
    applicationTracker: `in`.jansetu.app.tracker.ApplicationTracker?, documentVault: VaultDependencies?) {
    val context = LocalContext.current
    val darkTheme = JanSetuTheme.isDark
    val motion = JanSetuTheme.motion
    SideEffect {
        (context as? ComponentActivity)?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    val repository = remember { LocalCatalogRepository(context.applicationContext) }
    val scope = rememberCoroutineScope()
    // Inline confirmation for tracker saves and deletes. Shown by the screen it
    // targets ("list" or "detail:<id>") and cleared when that screen closes.
    var trackerNotice by remember { mutableStateOf<Pair<String, String>?>(null) }
    fun noticeFor(target: String) = trackerNotice?.takeIf { it.first == target }?.second
    fun noticeDone(target: String, message: String) { if (trackerNotice == target to message) trackerNotice = null }
    val tracker=remember(applicationTracker) {applicationTracker ?: TrackerDependencies.get(context).tracker}
    var trackerRefresh by remember {mutableIntStateOf(0)}
    // My Documents has its own storage and key; nothing is shared with the tracker above.
    val vault=remember(documentVault) {documentVault ?: VaultDependencies.get(context)}
    var documentNotice by remember { mutableStateOf<Pair<String, String>?>(null) }
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer=LifecycleEventObserver {_,event -> if(event==Lifecycle.Event.ON_RESUME) trackerRefresh++}
        lifecycle.addObserver(observer)
        onDispose {lifecycle.removeObserver(observer)}
    }
    LaunchedEffect(trackerRefresh) {withContext(Dispatchers.IO) {runCatching {tracker.reconcile()}}}
    var updating by remember { mutableStateOf(false) }
    var catalogMessage by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    var catalogState by remember { mutableStateOf<CatalogState>(CatalogState.Loading) }
    LaunchedEffect(reload) {
        catalogState = CatalogState.Loading
        catalogState = try { CatalogState.Ready(repository.load(), repository.source, repository.recoveredInvalidUpdate) }
        catch (error: CancellationException) { throw error }
        catch (_: Exception) { CatalogState.Failed(t(settings, "The offline catalog could not be loaded. Restart JanSetu or reinstall the local APK.", "ऑफ़लाइन सूची लोड नहीं हुई। जनसेतु फिर खोलें या स्थानीय APK दोबारा स्थापित करें।")) }
    }
    val importCatalog = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null && !updating) scope.launch {
            updating = true
            try {
                val current = (catalogState as? CatalogState.Ready)?.catalog ?: error("Catalog unavailable")
                val input = context.contentResolver.openInputStream(uri) ?: error("File unavailable")
                val updated = repository.install(input, current.version)
                catalogMessage = t(settings, "Catalog version ${updated.version} installed. It is available offline.", "सूची संस्करण ${updated.version} स्थापित हुआ। यह ऑफलाइन उपलब्ध है।")
                catalogState = CatalogState.Ready(updated, repository.source, repository.recoveredInvalidUpdate)
            } catch (error: CancellationException) { throw error }
            catch (_: Exception) {
                catalogMessage = t(settings, "This update could not be installed. Choose a newer signed JanSetu file. Your current catalog is unchanged.", "यह अपडेट स्थापित नहीं हुआ। नया हस्ताक्षरित जनसेतु फ़ाइल चुनें। आपकी वर्तमान सूची सुरक्षित है।")
            } finally { updating = false }
        }
    }
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route?.substringBefore("?")?.substringBefore("/")
    val topLevel = setOf("home", "search", "categories", "saved", "applications", "documents")
    // Five labels cannot remain legible on a narrow display or with large text.
    // In that case the bar shows icons only (even the selected label clipped at
    // 2x on 320dp), exposing every destination through its icon description.
    val density = LocalDensity.current
    val compactNavigation = with(density) { LocalWindowInfo.current.containerSize.width.toDp() } < 400.dp ||
        density.fontScale > 1.3f
    LaunchedEffect(requestedApplication) {
        if(requestedApplication!=null) nav.navigate("application/$requestedApplication") {launchSingleTop=true}
    }

    fun openWebsite(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addCategory(Intent.CATEGORY_BROWSABLE)
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, if (settings.language == "hi") "ब्राउज़र उपलब्ध नहीं है" else "No browser is available", Toast.LENGTH_LONG).show()
        }
    }

    fun openSupportEmail(portalTitle: String? = null, portalUrl: String? = null) {
        val subject = if (portalTitle == null) "JanSetu support" else "JanSetu portal report: $portalTitle"
        val body = if (portalUrl == null) "Please describe your question or issue:\n\n" else
            "Portal: $portalTitle\nURL: $portalUrl\n\nPlease describe the issue:\n\n"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:utkarshvaibhav268@gmail.com")).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, t(settings, "No email app is available", "ईमेल ऐप उपलब्ध नहीं है"), Toast.LENGTH_LONG).show()
        }
    }

    fun search(query: String = "", category: String = "") =
        nav.navigate("search?category=${Uri.encode(category)}&query=${Uri.encode(query)}")

    Scaffold(
        bottomBar = {
            if (route in topLevel) {
                NavigationBar {
                    listOf(
                        Triple("home", Icons.Default.Home, t(settings, "Home", "होम")),
                        Triple("search", Icons.Default.Search, t(settings, "Search", "खोजें")),
                        Triple("categories", Icons.Default.ViewModule, t(settings, "Browse", "श्रेणियाँ")),
                        Triple("saved", Icons.Default.Favorite, t(settings, "Saved", "सहेजे")),
                        Triple("applications", Icons.AutoMirrored.Filled.Assignment, t(settings, "Applications", "आवेदन"))
                    ).forEach { (destination, icon, label) ->
                        NavigationBarItem(
                            selected = route == destination,
                            onClick = {
                                if (route != destination) nav.navigate(destination) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    // Home is the retained root. Restoring its
                                    // saved stack can reopen a child Search route.
                                    restoreState = destination != "home"
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            // Narrow screens or large text: icons only. Each icon keeps
                            // its accessible name and every destination has a heading.
                            label = if (compactNavigation) null else ({ androidx.compose.material3.Text(label, maxLines = 1) }),
                            alwaysShowLabel = !compactNavigation
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = if (settings.onboardingComplete) "home" else "onboarding",
            modifier = Modifier.padding(padding),
            // Short cross-fades orient the person without delaying input. With
            // Reduce motion (or Android animations off) screens switch instantly.
            enterTransition = { if (motion.reduced) EnterTransition.None else fadeIn(tween(180)) },
            exitTransition = { if (motion.reduced) ExitTransition.None else fadeOut(tween(120)) },
            popEnterTransition = { if (motion.reduced) EnterTransition.None else fadeIn(tween(180)) },
            popExitTransition = { if (motion.reduced) ExitTransition.None else fadeOut(tween(120)) }
        ) {
            composable("onboarding") {
                OnboardingScreen(settings) {
                    settings.finishOnboarding()
                    nav.navigate("home") { popUpTo("onboarding") { inclusive = true } }
                }
            }
            composable("home") {
                when (val state = catalogState) {
                    CatalogState.Loading -> LoadingScreen(settings)
                    is CatalogState.Failed -> `in`.jansetu.app.ui.ErrorScreen(settings, state.message) { reload++ }
                    is CatalogState.Ready -> HomeScreen(
                        state.catalog, settings,
                        onSearch = { search() },
                        onCategory = { search(category = it) },
                        onDetail = { nav.navigate("detail/$it") },
                        onSettings = { nav.navigate("settings") },
                        onBrowse = { nav.navigate("categories") },
                        onApplications = { nav.navigate("applications") },
                        onQuery = { search(query = it) },
                        onHelp = { nav.navigate("help") },
                        onDocuments = { nav.navigate("documents") }
                    )
                }
            }
            composable(
                route = "search?category={category}&query={query}",
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType; defaultValue = "" },
                    navArgument("query") { type = NavType.StringType; defaultValue = "" }
                )
            ) { entry ->
                when (val state = catalogState) {
                    CatalogState.Loading -> LoadingScreen(settings)
                    is CatalogState.Failed -> `in`.jansetu.app.ui.ErrorScreen(settings, state.message) { reload++ }
                    is CatalogState.Ready -> SearchScreen(
                        state.catalog, settings, entry.arguments?.getString("category").orEmpty(),
                        onDetail = { nav.navigate("detail/$it") },
                        onBrowse = { nav.navigate("categories") },
                        onService = { nav.navigate("service/$it") },
                        initialQuery = entry.arguments?.getString("query").orEmpty()
                    )
                }
            }
            composable("categories") {
                when (val state = catalogState) {
                    CatalogState.Loading -> LoadingScreen(settings)
                    is CatalogState.Failed -> `in`.jansetu.app.ui.ErrorScreen(settings, state.message) { reload++ }
                    is CatalogState.Ready -> CategoriesScreen(state.catalog, settings, onOrganizations = { nav.navigate("organizations") },
                        onHelp = { nav.navigate("help") }) {
                        search(category = it)
                    }
                }
            }
            composable("saved") {
                when (val state = catalogState) {
                    CatalogState.Loading -> LoadingScreen(settings)
                    is CatalogState.Failed -> `in`.jansetu.app.ui.ErrorScreen(settings, state.message) { reload++ }
                    is CatalogState.Ready -> SavedScreen(state.catalog, settings, onSearch = { search() }) { nav.navigate("detail/$it") }
                }
            }
            composable("detail/{id}") { entry ->
                val catalog = (catalogState as? CatalogState.Ready)?.catalog
                val site = catalog?.websites?.firstOrNull { it.id == entry.arguments?.getString("id") }
                val guide = catalog?.guides?.firstOrNull { it.websiteId == site?.id }
                DetailScreen(
                    site, guide, settings,
                    onBack = { nav.popBackStack() },
                    onOpen = {
                        if (site != null) {
                            if (site.browserSupport == "in_app") nav.navigate("browser/${site.id}")
                            else openWebsite(site.url)
                        }
                    },
                    onSource = { if (site != null) openWebsite(site.sourceUrl) },
                    onGuideSource = { if (guide != null) openWebsite(guide.sourceUrl) },
                    onReport = { if (site != null) openSupportEmail(site.title, site.url) },
                    categories = catalog?.categories.orEmpty(),
                    profile = if (site != null) catalog?.profileFor(site.id) else null,
                    services = if (site != null) catalog?.servicesFor(site.id).orEmpty() else emptyList(),
                    onService = { nav.navigate("service/$it") },
                    onLink = ::openWebsite
                )
            }
            composable("browser/{id}") { entry ->
                when (val state = catalogState) {
                    CatalogState.Loading -> LoadingScreen(settings)
                    is CatalogState.Failed -> `in`.jansetu.app.ui.ErrorScreen(settings, state.message) { reload++ }
                    is CatalogState.Ready -> {
                        val site = state.catalog.websites.firstOrNull { it.id == entry.arguments?.getString("id") }
                        if (site != null && site.browserSupport == "in_app") {
                            BrowserScreen(site, settings, onClose = { nav.popBackStack() }, onExternal = ::openWebsite)
                        } else {
                            `in`.jansetu.app.ui.ErrorScreen(settings, t(settings, "This website cannot open inside JanSetu.", "यह वेबसाइट जनसेतु के अंदर नहीं खुल सकती।")) { nav.popBackStack() }
                        }
                    }
                }
            }
            composable("settings") {
                SettingsScreen(settings, (catalogState as? CatalogState.Ready)?.catalog,
                    onBack = { nav.popBackStack() }, onPrivacy = { nav.navigate("privacy") },
                    onSupport = { openSupportEmail() }, onCatalog = { nav.navigate("catalog") },
                    onApplications = { nav.navigate("applications") }, onHelp = { nav.navigate("help") },
                    notificationsAvailable = { tracker.notificationsAvailable() }, onDocuments = { nav.navigate("documents") })
            }
            composable("help") {
                HelpScreen(settings, (catalogState as? CatalogState.Ready)?.catalog, onBack = { nav.popBackStack() },
                    onGuide = { nav.navigate("detail/$it") }, onSearch = { search() }, onPrivacy = { nav.navigate("privacy") },
                    onSupport = { openSupportEmail() })
            }
            composable("catalog") {
                val ready = catalogState as? CatalogState.Ready
                CatalogManagementScreen(ready?.catalog, settings,
                    ready?.source ?: "bundled", updating, catalogMessage, ready?.recovered ?: false,
                    onBack = { nav.popBackStack() },
                    onImport = { importCatalog.launch(arrayOf("application/json", "application/octet-stream", "*/*")) },
                    onRestore = { if (!updating) scope.launch {
                        updating = true
                        try {
                            repository.restoreBundled()
                            val restored = repository.load()
                            catalogState = CatalogState.Ready(restored, repository.source, repository.recoveredInvalidUpdate)
                            catalogMessage = t(settings, "Bundled catalog restored.", "ऐप की मूल सूची बहाल हुई।")
                        } catch (error: CancellationException) { throw error }
                        catch (_: Exception) { catalogMessage = t(settings, "The bundled catalog could not be restored. Restart JanSetu to recover it.", "मूल सूची बहाल नहीं हुई। पुनर्प्राप्ति के लिए जनसेतु फिर खोलें।") }
                        finally { updating = false }
                    } })
            }
            composable("organizations") {
                val catalog = (catalogState as? CatalogState.Ready)?.catalog
                if (catalog != null) OrganizationsScreen(catalog, settings,
                    onBack = { nav.popBackStack() }, onDetail = { nav.navigate("detail/$it") })
            }
            composable("service/{id}") { entry ->
                val services=(catalogState as? CatalogState.Ready)?.catalog?.services.orEmpty()
                val service=services.firstOrNull {it.id==entry.arguments?.getString("id")}
                `in`.jansetu.app.ui.ServiceScreen(service,settings,onBack={nav.popBackStack()},
                    onWebsite={if(service!=null) nav.navigate("detail/${service.websiteId}")},onLink=::openWebsite,
                    onAddApplication=if(service?.hasConfirmedApplicationRoute()==true) ({nav.navigate("application-edit?service=${service.id}")}) else null,
                    onPrepare=if(service!=null) ({nav.navigate("prepare/${service.id}")}) else null,related=services)
            }
            composable("applications") {
                val notice=noticeFor("list")
                MyApplicationsScreen(settings,tracker,trackerRefresh,onAdd={nav.navigate("application-edit")},onOpen={nav.navigate("application/$it")},
                    notice=notice,onNoticeDone={if(notice!=null) noticeDone("list",notice)})
            }
            composable("application-edit?id={id}&service={service}",arguments=listOf(
                navArgument("id") {type=NavType.StringType;defaultValue=""},navArgument("service") {type=NavType.StringType;defaultValue=""}
            )) {entry ->
                val id=entry.arguments?.getString("id")?.takeIf {it.isNotBlank()}
                val serviceId=entry.arguments?.getString("service")?.takeIf {it.isNotBlank()}
                val catalog=(catalogState as? CatalogState.Ready)?.catalog
                val service=catalog?.services?.firstOrNull {it.id==serviceId && it.hasConfirmedApplicationRoute()}
                if(serviceId!=null && service==null) {
                    ServiceUnavailableForTracking(onBack={nav.popBackStack()})
                } else ApplicationEditorScreen(tracker,id,service?.trackerSnapshot(requireNotNull(catalog).version),onBack={nav.popBackStack()},onSaved={saved ->
                    trackerNotice="detail:$saved" to (if(id==null) "Application saved on this device." else "Changes saved. Any status change was added to your history.")
                    trackerRefresh++;nav.popBackStack();nav.navigate("application/$saved") {launchSingleTop=true}
                })
            }
            composable("application/{id}") {entry ->
                val appId=entry.arguments?.getString("id").orEmpty()
                val notice=noticeFor("detail:$appId")
                ApplicationDetailScreen(appId,tracker,(catalogState as? CatalogState.Ready)?.catalog,trackerRefresh,
                    onBack={nav.popBackStack()},onEdit={nav.navigate("application-edit?id=$appId")},
                    onDeleted={trackerNotice="list" to "Application deleted from this device.";trackerRefresh++;nav.popBackStack();nav.navigate("applications") {launchSingleTop=true}},onLink=::openWebsite,
                    notice=notice,onNoticeDone={if(notice!=null) noticeDone("detail:$appId",notice)},
                    vault=vault,onPrepare={serviceId -> nav.navigate("prepare/$serviceId")})
            }
            composable("documents") {
                val notice=documentNotice?.takeIf {it.first=="list"}?.second
                MyDocumentsScreen(settings,vault,trackerRefresh,onOpen={nav.navigate("document/$it")},
                    notice=notice,onNoticeDone={if(notice!=null && documentNotice?.second==notice) documentNotice=null})
            }
            composable("document/{id}") {entry ->
                DocumentDetailScreen(entry.arguments?.getString("id").orEmpty(),vault,trackerRefresh,onBack={nav.popBackStack()},
                    onDeleted={documentNotice="list" to "Document deleted from this device.";nav.popBackStack()})
            }
            composable("prepare/{serviceId}") {entry ->
                val service=(catalogState as? CatalogState.Ready)?.catalog?.services?.firstOrNull {it.id==entry.arguments?.getString("serviceId")}
                PrepareDocumentsScreen(service,settings,vault,trackerRefresh,onBack={nav.popBackStack()},onLink=::openWebsite,
                    onDocuments={nav.navigate("documents") {launchSingleTop=true}},
                    onTrack=if(service?.hasConfirmedApplicationRoute()==true) ({nav.navigate("application-edit?service=${service.id}")}) else null)
            }
            composable("privacy") { PrivacyScreen(settings, onBack = { nav.popBackStack() }) }
        }
    }
}
