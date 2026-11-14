package `in`.jansetu.app.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import `in`.jansetu.app.catalog.Catalog
import `in`.jansetu.app.catalog.Category
import `in`.jansetu.app.catalog.CitizenSearch
import `in`.jansetu.app.catalog.DiscoveryResult
import `in`.jansetu.app.catalog.Guide
import `in`.jansetu.app.catalog.Service
import `in`.jansetu.app.catalog.Website
import `in`.jansetu.app.catalog.WebsiteProfile
import `in`.jansetu.app.catalog.citizenJurisdictionLevel
import `in`.jansetu.app.catalog.profileFor
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.settings.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun t(settings: LocalSettings, english: String, hindi: String): String =
    if (settings.language == "hi") hindi else english

private fun portalDescription(site: Website, settings: LocalSettings): String = when {
    settings.language != "hi" -> site.description
    site.isDirectoryListed -> "IGOD की निर्देशिका में यह वेबसाइट सूचीबद्ध है। वर्तमान जानकारी के लिए आधिकारिक साइट खोलें।"
    else -> site.descriptionHi ?: site.description
}

internal fun dateLabel(value: String?, settings: LocalSettings): String = runCatching {
    LocalDate.parse(value?.take(10)).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.forLanguageTag(settings.language)))
}.getOrDefault(t(settings,"Not available","उपलब्ध नहीं"))

private fun jurisdiction(site: Website) = listOfNotNull(site.level, site.state, site.district).distinct().joinToString(" · ")

/** Consistent Material icons for catalog categories, with a generic fallback. */
fun categoryIcon(id: String): ImageVector = when (id) {
    "documents" -> Icons.Default.Badge
    "grievances" -> Icons.Default.ReportProblem
    "jobs" -> Icons.Default.Work
    "information" -> Icons.Default.Info
    "education" -> Icons.Default.School
    "business" -> Icons.Default.Storefront
    "taxation" -> Icons.Default.CurrencyRupee
    "transparency" -> Icons.Default.QuestionAnswer
    "agriculture" -> Icons.Default.Agriculture
    "transport" -> Icons.Default.DirectionsCar
    "courts" -> Icons.Default.Gavel
    "health" -> Icons.Default.LocalHospital
    "welfare" -> Icons.Default.VolunteerActivism
    "land" -> Icons.Default.HomeWork
    "utilities" -> Icons.Default.WaterDrop
    "civic" -> Icons.Default.LocationCity
    "environment" -> Icons.Default.Forest
    "industry" -> Icons.Default.Factory
    "safety" -> Icons.Default.Shield
    else -> Icons.Default.Category
}

@Composable
private fun Page(modifier: Modifier = Modifier, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    JanSetuPage(modifier = modifier, content = content)
}

@Composable
private fun SectionTitle(title: String, trailing: String? = null, onTrailing: (() -> Unit)? = null) {
    JanSetuSectionTitle(title, trailing, onTrailing)
}

@Composable
private fun Header(title: String, settings: LocalSettings, onBack: (() -> Unit)? = null, onSettings: (() -> Unit)? = null, subtitle: String? = null) {
    JanSetuScreenHeader(
        title = title,
        subtitle = subtitle,
        backLabel = if (settings.language == "hi") "पीछे" else "Back",
        onBack = onBack,
        action = if (onSettings == null) null else {
            { IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, contentDescription = if (settings.language == "hi") "सेटिंग्स" else "Settings") } }
        }
    )
}

@Composable
private fun EmptyCard(title: String, message: String, action: String? = null, onAction: (() -> Unit)? = null, illustration: Illustration? = null) {
    JanSetuEmptyState(title, message, action, onAction, illustration)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PortalCard(site: Website, settings: LocalSettings, profile: WebsiteProfile? = null, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().testTag("website:${site.id}"),
        shape = JanSetuLayout.cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    JanSetuStatusLabel(t(settings, "Website", "वेबसाइट"), StatusTone.NEUTRAL, Icons.Default.Language)
                    InformationStatusLabel(site, profile, settings)
                }
                Text(site.title, style = MaterialTheme.typography.titleMedium)
                // A checked purpose replaces the directory template; otherwise the listing text is shown as is.
                Text(profile?.purpose?.value ?: portalDescription(site,settings), maxLines = 2, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                jurisdiction(site).takeIf { it.isNotEmpty() }?.let { JanSetuBadge(it, Icons.Default.Place) }
                Text(site.domain + " · " + if (site.isDirectoryListed) t(settings, "Listed by IGOD", "IGOD में सूचीबद्ध") else t(settings, "Source checked", "स्रोत जाँचा गया"),
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun OnboardingScreen(settings: LocalSettings, onContinue: () -> Unit) {
    var chooseState by remember { mutableStateOf(false) }
    val motion = JanSetuTheme.motion
    // A single, short entrance for the illustration. It never loops and is
    // skipped entirely with Reduce motion or Android animations turned off.
    val reveal = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(motion.reduced) { if (!motion.reduced) reveal.animateTo(1f, motion.standard(450)) else reveal.snapTo(1f) }
    // The content scrolls; the two actions stay pinned so they are never below the fold.
    Column(Modifier.fillMaxSize()) {
    Box(Modifier.weight(1f)) { Page {
        JanSetuWordmark(tagline = t(settings, "Independent guide to public services", "सरकारी सेवाओं के लिए स्वतंत्र मार्गदर्शक"))
        if (LocalDensity.current.fontScale <= 1.3f) JanSetuIllustration(Illustration.HERO, Modifier.fillMaxWidth().height(120.dp)
            .graphicsLayer { alpha = reveal.value; translationY = (1f - reveal.value) * 24f })
        Text(t(settings, "A clearer path to public services", "सरकारी सेवाओं तक आसान पहुँच"),
            style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics { heading() })
        OnboardingStep(Icons.Default.Search, t(settings, "Describe what you need", "बताइए आपको क्या चाहिए"),
            t(settings, "Search in plain words, such as “income certificate”. JanSetu shows official websites and checked service guides.",
                "सरल शब्दों में खोजें, जैसे “आय प्रमाण पत्र”। जनसेतु आधिकारिक वेबसाइटें और जाँची गई सेवा जानकारी दिखाता है।"))
        OnboardingStep(Icons.AutoMirrored.Filled.OpenInNew, t(settings, "Continue on the official website", "आधिकारिक वेबसाइट पर आगे बढ़ें"),
            t(settings, "Applications, payments and sign-in happen on the government's own site. JanSetu never asks for passwords or OTPs.",
                "आवेदन, भुगतान और साइन-इन सरकार की अपनी साइट पर होते हैं। जनसेतु कभी पासवर्ड या OTP नहीं माँगता।"))
        OnboardingStep(Icons.Default.Lock, t(settings, "Track your progress privately", "अपनी प्रगति निजी रूप से दर्ज करें"),
            t(settings, "My Applications keeps your own notes on this device only. No account is needed.",
                "मेरे आवेदन में आपके नोट केवल इसी डिवाइस पर रहते हैं। कोई खाता ज़रूरी नहीं।"))
        JanSetuNotice(t(settings, "JanSetu is independent. It is not a government app and is not endorsed by any government.",
            "जनसेतु स्वतंत्र है। यह सरकारी ऐप नहीं है और किसी सरकार द्वारा समर्थित नहीं है।"))
        HorizontalDivider()
        Text(t(settings, "Choose your language", "अपनी भाषा चुनें"), style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() })
        LanguageChoice(settings)
        Text(t(settings, "State or Union Territory (optional)", "राज्य या केंद्र शासित प्रदेश (वैकल्पिक)"),
            style = MaterialTheme.typography.titleMedium)
        OutlinedButton(onClick = { chooseState = true }) {
            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(settings.state)
        }
        Text(t(settings,
            "Regional coverage varies. Use the available state and district filters in Search.",
            "क्षेत्रीय कवरेज अलग-अलग है। खोज में उपलब्ध राज्य और जिला फ़िल्टर देखें।"),
            color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    } }
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.widthIn(max = JanSetuLayout.maxContentWidth).fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                Text(t(settings, "Explore JanSetu", "जनसेतु देखें"))
            }
            TextButton(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text(t(settings, "Skip preferences", "विकल्प छोड़ें"))
            }
        }
    }
    }
    if (chooseState) StateDialog(settings, onDismiss = { chooseState = false })
}

@Composable
private fun OnboardingStep(icon: ImageVector, title: String, body: String) {
    Row(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}, verticalAlignment = Alignment.Top) {
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
        }
        Spacer(Modifier.width(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LanguageChoice(settings: LocalSettings) {
    Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("en" to "English", "hi" to "हिन्दी").forEach { (code, label) ->
            FilterChip(selected = settings.language == code, onClick = { settings.updateLanguage(code) }, label = { Text(label) },
                leadingIcon = if (settings.language == code) ({ Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }) else null)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    catalog: Catalog, settings: LocalSettings,
    onSearch: () -> Unit, onCategory: (String) -> Unit, onDetail: (String) -> Unit,
    onSettings: () -> Unit, onBrowse: () -> Unit, onApplications: (() -> Unit)? = null,
    onQuery: (String) -> Unit = { onSearch() }, onHelp: (() -> Unit)? = null, onDocuments: (() -> Unit)? = null
) {
    Page(Modifier.testTag("home-content")) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            JanSetuWordmark(Modifier.weight(1f), tagline = t(settings, "Independent guide to public services", "सरकारी सेवाओं के लिए स्वतंत्र मार्गदर्शक"))
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, contentDescription = if (settings.language == "hi") "सेटिंग्स" else "Settings")
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = JanSetuLayout.cardShape) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Decorative; skipped at large text so the search task stays near the top.
                if (LocalDensity.current.fontScale <= 1.3f) JanSetuIllustration(Illustration.HERO, Modifier.fillMaxWidth().height(96.dp))
                Text(t(settings, "PUBLIC SERVICES, MADE CLEARER", "सरकारी सेवाएँ, सरल तरीके से"),
                    color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Text(t(settings, "What government service do you need?", "आप क्या करना चाहते हैं?"),
                    style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.semantics { heading() })
                Text(t(settings, "Describe it in your own words. JanSetu points you to official websites and checked service guides.",
                    "अपने शब्दों में लिखें। जनसेतु आपको आधिकारिक वेबसाइटों और जाँची गई सेवा जानकारी तक ले जाता है।"),
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Surface(onClick = onSearch, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(t(settings, "Search by need, service or portal name", "ज़रूरत या पोर्टल के नाम से खोजें"),
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionTitle(t(settings, "Common tasks", "आम काम"))
            Text(t(settings, "Chosen by JanSetu editors as starting points. Not based on usage data.",
                "शुरुआत के लिए जनसेतु संपादकों द्वारा चुने गए। ये उपयोग के आँकड़ों पर आधारित नहीं हैं।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HomeTaskShortcuts.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { task ->
                    TaskTile(t(settings, task.title, task.titleHi), task.illustration, Modifier.weight(1f)) { onQuery(task.query) }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        if (settings.recentSearches.isNotEmpty()) {
            SectionTitle(t(settings, "Recent searches", "हाल की खोजें"))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                settings.recentSearches.take(5).forEach { recent ->
                    AssistChip(onClick = { onQuery(recent) }, label = { Text(recent, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 220.dp)) },
                        leadingIcon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) })
                }
            }
        }
        if (onApplications != null) {
            OutlinedCard(onClick = onApplications, modifier = Modifier.fillMaxWidth(), shape = JanSetuLayout.cardShape) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(t(settings, "View My Applications", "मेरे आवेदन देखें"), style = MaterialTheme.typography.titleMedium)
                        Text(t(settings, "Keep a private record of applications you make on official websites. Stored only on this device.",
                            "आधिकारिक वेबसाइटों पर किए गए अपने आवेदनों का निजी रिकॉर्ड रखें। यह केवल इसी डिवाइस पर रहता है।"),
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        if (onDocuments != null) {
            OutlinedCard(onClick = onDocuments, modifier = Modifier.fillMaxWidth().testTag("home-documents"), shape = JanSetuLayout.cardShape) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(t(settings, "My Documents", "मेरे दस्तावेज़"), style = MaterialTheme.typography.titleMedium)
                        Text(t(settings, "Keep encrypted copies of certificates and photos you may need to upload. Stored only on this device.",
                            "अपलोड के लिए ज़रूरी प्रमाणपत्रों और फ़ोटो की एन्क्रिप्टेड प्रतियाँ रखें। केवल इसी डिवाइस पर।"),
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        SectionTitle(t(settings, "Browse by need", "ज़रूरत के अनुसार देखें"), t(settings, "See all", "सभी देखें"), onBrowse)
        catalog.categories.filter { category -> catalog.websites.any { category.id in it.categories } }.take(6).chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { category ->
                    CategoryCard(category, settings, catalog.websites.count { category.id in it.categories },
                        Modifier.weight(1f)) { onCategory(category.id) }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        SectionTitle(t(settings, "Reviewed websites", "सत्यापित पोर्टल"))
        catalog.websites.filter { !it.isDirectoryListed }.take(3).forEach {
            PortalCard(it, settings, catalog.profileFor(it.id)) { onDetail(it.id) }
        }
        val directoryCount = catalog.websites.count { it.isDirectoryListed }
        if (directoryCount > 0) {
            SectionTitle(t(settings, "Directory listings", "निर्देशिका की वेबसाइटें"),
                t(settings, "Browse directory", "निर्देशिका देखें"), onSearch)
            Text(t(settings, "$directoryCount websites listed by IGOD. Search by location or sector.",
                "IGOD में सूचीबद्ध $directoryCount वेबसाइटें। स्थान या क्षेत्र से खोजें।"),
                style = MaterialTheme.typography.bodyMedium)
            catalog.websites.asSequence().filter { it.isDirectoryListed }.take(3).forEach {
                PortalCard(it, settings, catalog.profileFor(it.id)) { onDetail(it.id) }
            }
        }
        val recentSites = settings.recent.mapNotNull { id -> catalog.websites.firstOrNull { it.id == id } }.take(2)
        if (recentSites.isNotEmpty()) {
            SectionTitle(t(settings, "Recently viewed", "हाल ही में देखे गए"))
            recentSites.forEach { PortalCard(it, settings, catalog.profileFor(it.id)) { onDetail(it.id) } }
        }
        val favoriteSites = catalog.websites.filter { it.id in settings.favorites }.take(2)
        if (favoriteSites.isNotEmpty()) {
            SectionTitle(t(settings, "Favorites", "पसंदीदा"))
            favoriteSites.forEach { PortalCard(it, settings, catalog.profileFor(it.id)) { onDetail(it.id) } }
        }
        JanSetuInfoCard(t(settings, "About JanSetu", "जनसेतु के बारे में"),
            t(settings, "JanSetu is an independent guide to public services. It is not a government app and is not endorsed by any government. Applications, payments and sign-in happen on official websites.",
                "जनसेतु सरकारी सेवाओं के लिए एक स्वतंत्र मार्गदर्शक है। यह सरकारी ऐप नहीं है और किसी सरकार द्वारा समर्थित नहीं है। आवेदन, भुगतान और साइन-इन आधिकारिक वेबसाइटों पर होते हैं।")) {
            if (onHelp != null) TextButton(onClick = onHelp) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(t(settings, "How JanSetu works", "जनसेतु कैसे काम करता है"))
            }
        }
    }
}

@Composable
private fun TaskTile(label: String, illustration: Illustration, modifier: Modifier, onClick: () -> Unit) {
    // The label is also the accessible name, so a tile cut off by the screen
    // edge (only its picture visible) is still announced correctly.
    OutlinedCard(onClick = onClick, modifier = modifier.heightIn(min = 120.dp).semantics { contentDescription = label }, shape = JanSetuLayout.cardShape) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            JanSetuIllustration(illustration, Modifier.size(52.dp))
            Text(label, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun CategoryCard(category: Category, settings: LocalSettings, count: Int, modifier: Modifier, onClick: () -> Unit) {
    val title = if (settings.language == "hi") category.titleHi else category.title
    val countLabel = t(settings, "$count ${if (count == 1) "portal" else "portals"}", "$count पोर्टल")
    OutlinedCard(onClick = onClick, modifier = modifier.heightIn(min = 120.dp).semantics { contentDescription = "$title, $countLabel" }, shape = JanSetuLayout.cardShape) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(categoryIcon(category.id), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                }
            }
            Text(if (settings.language == "hi") category.titleHi else category.title,
                style = MaterialTheme.typography.titleMedium)
            Text(t(settings, "$count ${if (count == 1) "portal" else "portals"}", "$count पोर्टल"),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(catalog: Catalog, settings: LocalSettings, categoryId: String, onDetail: (String) -> Unit, onBrowse: () -> Unit,
                 onService: (String) -> Unit = {}, initialQuery: String = "") {
    var query by rememberSaveable(categoryId, initialQuery) { mutableStateOf(initialQuery) }
    var selectedCategory by rememberSaveable(categoryId) { mutableStateOf(categoryId.takeIf { id -> catalog.categories.any { it.id == id } }) }
    var selectedLevel by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedState by rememberSaveable { mutableStateOf(settings.state.takeIf { value -> catalog.websites.any { it.state == value } }) }
    var selectedDistrict by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedType by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmClearHistory by remember { mutableStateOf(false) }
    val levels = remember(catalog) { (catalog.websites.map { it.level } + catalog.services.map { it.citizenJurisdictionLevel() }).distinct().sorted() }
    val states = remember(catalog) { catalog.websites.mapNotNull { it.state }.distinct().sorted() }
    val categories = remember(catalog) { catalog.categories.filter { c -> catalog.websites.any { c.id in it.categories } } }
    val districts = remember(catalog, selectedState) {
        if (selectedState == null) emptyList() else catalog.websites.filter { it.state == selectedState }.mapNotNull { it.district }.distinct().sorted()
    }
    val results by produceState<List<DiscoveryResult>?>(null, catalog, query, selectedCategory, selectedLevel, selectedState, selectedDistrict, selectedType) {
        value = null
        if (query.isNotBlank()) delay(120)
        value = withContext(Dispatchers.Default) {
            CitizenSearch.find(catalog, query, selectedCategory, selectedLevel, selectedState, selectedDistrict, selectedType ?: "all")
        }
    }
    val currentResults = results
    val focus = LocalFocusManager.current
    val motion = JanSetuTheme.motion
    val filtersActive = selectedCategory != null || selectedLevel != null || selectedState != null || selectedDistrict != null || selectedType != null
    fun clearFilters() {
        selectedCategory = null
        selectedLevel = null
        selectedState = null
        selectedDistrict = null
        selectedType = null
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(Modifier.widthIn(max = 720.dp).fillMaxSize(), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Header(t(settings, "Search portals", "पोर्टल खोजें"), settings)
                    Text(t(settings,"Find verified services and general websites. A website listing is not a service or an eligibility decision.","सत्यापित सेवाएँ और सामान्य वेबसाइटें खोजें। वेबसाइट की सूची किसी सेवा या पात्रता निर्णय का प्रमाण नहीं है।"),
                        style=MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(value = query, onValueChange = { query = it }, singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { settings.recordSearch(query); focus.clearFocus() }),
                        label = { Text(t(settings, "What government service do you need?", "आप क्या खोज रहे हैं?")) },
                        placeholder = { Text(t(settings, "For example: income certificate", "उदाहरण: आय प्रमाण पत्र")) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = if (query.isEmpty()) null else ({
                            IconButton(onClick = { query = "" }) { Icon(Icons.Default.Clear, contentDescription = t(settings, "Clear search text", "खोज का पाठ मिटाएँ")) }
                        }),
                        shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth())
                    if (query.isBlank() && settings.recentSearches.isNotEmpty()) {
                        Text(t(settings, "Recent searches", "हाल की खोजें"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            settings.recentSearches.forEach { recent ->
                                AssistChip(onClick = { query = recent }, label = { Text(recent,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.widthIn(max=220.dp)) },
                                    leadingIcon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) })
                            }
                        }
                        TextButton(onClick = { confirmClearHistory = true }) { Text(t(settings,"Clear search and viewing history","खोज और देखने का इतिहास मिटाएँ")) }
                    } else if (query.length in 2..5 && !currentResults.isNullOrEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            currentResults.take(3).forEach { suggestion ->
                                AssistChip(onClick = { query = suggestion.title }, label = { Text(suggestion.title,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.widthIn(max=220.dp)) })
                            }
                        }
                    }
                    Text(t(settings, "Filters", "फ़िल्टर"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics { heading() })
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if(catalog.services.isNotEmpty()) FilterMenuChip(t(settings,"Services and websites","सेवाएँ और वेबसाइटें"),selectedType,
                            listOf("services" to t(settings,"Verified services","सत्यापित सेवाएँ"),"websites" to t(settings,"Websites only","केवल वेबसाइटें"),
                                "checked" to t(settings,"Websites with checked information","जाँची गई जानकारी वाली वेबसाइटें")), settings) {selectedType=it}
                        FilterMenuChip(t(settings, "All sectors", "सभी क्षेत्र"), selectedCategory,
                            categories.map { it.id to if (settings.language == "hi") it.titleHi else it.title }, settings) { selectedCategory = it }
                        if (levels.size > 1) FilterMenuChip(t(settings, "All levels", "सभी स्तर"), selectedLevel, levels.map { it to it }, settings) { selectedLevel = it }
                        if (states.isNotEmpty()) FilterMenuChip(t(settings, "All states / UTs", "सभी राज्य / केंद्र शासित प्रदेश"), selectedState,
                            states.map { it to it }, settings) { selectedState = it; selectedDistrict = null }
                        if (districts.isNotEmpty()) FilterMenuChip(t(settings, "All districts", "सभी जिले"), selectedDistrict, districts.map { it to it }, settings) { selectedDistrict = it }
                    }
                    AnimatedVisibility(filtersActive, enter = fadeIn(motion.standard()), exit = fadeOut(motion.standard())) {
                        AssistChip(onClick = ::clearFilters, modifier = Modifier.testTag("clear-search-filters"),
                            label = { Text(t(settings, "Clear filters", "फ़िल्टर हटाएँ")) },
                            leadingIcon = { Icon(Icons.Default.FilterAltOff, contentDescription = null, modifier = Modifier.size(18.dp)) })
                    }
                    if (selectedState != null) JanSetuNotice(t(settings,
                        "Showing records listed for $selectedState. Central services and nationwide websites are hidden while a state filter is on.",
                        "$selectedState के लिए सूचीबद्ध रिकॉर्ड दिख रहे हैं। राज्य फ़िल्टर चालू रहते केंद्रीय सेवाएँ और राष्ट्रीय वेबसाइटें नहीं दिखतीं।"),
                        tone = StatusTone.INFO)
                    val listed = currentResults?.count { it.service==null && it.website.isDirectoryListed } ?: 0
                    val serviceCount=currentResults?.count {it.service!=null} ?: 0
                    val websiteCount=(currentResults?.size ?: 0)-serviceCount
                    if (currentResults == null) JanSetuLoading(t(settings, "Searching portals…", "पोर्टल खोज रहे हैं…"))
                    else Text((if(serviceCount>0) t(settings,"$serviceCount verified ${if (serviceCount == 1) "service" else "services"} · ","$serviceCount सत्यापित सेवाएँ · ") else "")+
                        t(settings, "$websiteCount ${if (websiteCount == 1) "portal" else "portals"} · $listed directory listed", "$websiteCount पोर्टल · $listed निर्देशिका में सूचीबद्ध"),
                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (currentResults != null && currentResults.isEmpty()) item {
                EmptyCard(t(settings, "No matching portals", "कोई पोर्टल नहीं मिला"),
                    if (selectedState != null) t(settings,
                        "Nothing listed for $selectedState matches this search. Coverage differs by state and is incomplete. Try another term or clear the filters.",
                        "$selectedState के लिए इस खोज से मेल खाता कुछ सूचीबद्ध नहीं है। कवरेज राज्य के अनुसार अलग और अधूरा है। दूसरा शब्द आज़माएँ या फ़िल्टर हटाएँ।")
                    else t(settings, "Try another term or clear a filter. Coverage varies by location and sector.", "दूसरा शब्द आज़माएँ या फ़िल्टर हटाएँ। स्थान और क्षेत्र के अनुसार कवरेज अलग है।"),
                    if (filtersActive) t(settings, "Clear all filters", "सभी फ़िल्टर हटाएँ") else t(settings, "Browse categories", "श्रेणियाँ देखें"),
                    if (filtersActive) ::clearFilters else onBrowse, Illustration.NO_RESULTS)
            } else if (currentResults != null) items(currentResults, key = { it.id }) { result ->
                if(result.service!=null) ServiceResultCard(result.service,settings) {settings.recordSearch(query);onService(result.service.id)}
                else PortalCard(result.website, settings, catalog.profileFor(result.website.id)) { settings.recordSearch(query); onDetail(result.website.id) }
            }
        }
    }
    if (confirmClearHistory) JanSetuConfirmDialog(
        t(settings, "Clear history?", "इतिहास मिटाएँ?"),
        t(settings, "Recent searches and recently viewed portals will be removed from this device. Favorites, settings and My Applications are not affected.",
            "हाल की खोजें और हाल में देखे गए पोर्टल इस डिवाइस से हट जाएँगे। पसंदीदा, सेटिंग्स और मेरे आवेदन पर असर नहीं पड़ेगा।"),
        t(settings, "Clear history", "इतिहास मिटाएँ"), t(settings, "Cancel", "रद्द करें"),
        onConfirm = { settings.clearHistory(); confirmClearHistory = false }, onDismiss = { confirmClearHistory = false })
}

/**
 * A filter shown as a chip that opens a menu. The chip names its current
 * value; an applied filter also shows a check mark so state is not colour-only.
 */
@Composable
private fun FilterMenuChip(allLabel: String, selected: String?, options: List<Pair<String, String>>, settings: LocalSettings, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val current = options.firstOrNull { it.first == selected }?.second
    Box {
        AssistChip(
            onClick = { expanded = true },
            modifier = Modifier.testTag("filter:$allLabel").semantics {
                stateDescription = if (current == null) t(settings, "No filter", "कोई फ़िल्टर नहीं") else t(settings, "Filter applied", "फ़िल्टर लागू")
            },
            label = { Text(current ?: allLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 200.dp)) },
            leadingIcon = if (current != null) ({ Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }) else null,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp)) },
            colors = if (current != null) AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer, labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer, trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            else AssistChipDefaults.assistChipColors(),
            border = if (current != null) null else AssistChipDefaults.assistChipBorder(enabled = true)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text(allLabel) }, onClick = { onSelect(null); expanded = false },
                leadingIcon = if (selected == null) ({ Icon(Icons.Default.Check, contentDescription = null) }) else null)
            options.forEach { (id, label) ->
                DropdownMenuItem(text = { Text(label) }, onClick = { onSelect(id); expanded = false },
                    leadingIcon = if (selected == id) ({ Icon(Icons.Default.Check, contentDescription = null) }) else null)
            }
        }
    }
}

@Composable
fun CategoriesScreen(catalog: Catalog, settings: LocalSettings, onOrganizations: (() -> Unit)? = null, onHelp: (() -> Unit)? = null,
                     onCategory: (String) -> Unit) {
    val counts=remember(catalog) { catalog.websites.flatMap { it.categories }.groupingBy { it }.eachCount() }
    val ordered=remember(catalog,counts) { catalog.categories.filter { (counts[it.id] ?: 0)>0 }.sortedBy { it.parentId.orEmpty() } }
    Page {
        Header(t(settings, "Browse by need", "ज़रूरत के अनुसार देखें"), settings)
        Text(t(settings, "Choose a topic to see the portals currently available.",
            "उपलब्ध पोर्टल देखने के लिए विषय चुनें।"),
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (onOrganizations != null) OutlinedButton(onClick=onOrganizations) { Text(t(settings,"Browse organizations","संगठन देखें")) }
            if (onHelp != null) TextButton(onClick = onHelp) { Text(t(settings, "Help & guides", "सहायता और मार्गदर्शिकाएँ")) }
        }
        ordered.forEachIndexed { index, category ->
            if(category.parentId!=null && (index==0 || ordered[index-1].parentId!=category.parentId)) {
                catalog.categoryGroups.firstOrNull { it.id==category.parentId }?.let { group -> SectionTitle(if(settings.language=="hi") group.titleHi else group.title) }
            }
            OutlinedCard(onClick = { onCategory(category.id) }, modifier = Modifier.fillMaxWidth(), shape = JanSetuLayout.cardShape) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(categoryIcon(category.id), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(if (settings.language == "hi") category.titleHi else category.title, style = MaterialTheme.typography.titleMedium)
                        val count = counts[category.id] ?: 0
                        Text(t(settings, "$count ${if (count == 1) "portal" else "portals"}", "$count पोर्टल"),
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun SavedScreen(catalog: Catalog, settings: LocalSettings, onSearch: (() -> Unit)? = null, onDetail: (String) -> Unit) {
    var showRecent by rememberSaveable { mutableStateOf(false) }
    val ids = if (showRecent) settings.recent else settings.favorites.toList()
    val byId=remember(catalog) {catalog.websites.associateBy {it.id}}
    val sites = ids.mapNotNull { byId[it] }
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item {Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Header(t(settings, "Saved & recent", "सहेजे गए और हाल के"), settings)
            Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !showRecent, onClick = { showRecent = false },
                    label = { Text(t(settings, "Favorites", "पसंदीदा")) },
                    leadingIcon = if (!showRecent) ({ Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }) else null)
                FilterChip(selected = showRecent, onClick = { showRecent = true },
                    label = { Text(t(settings, "Recently viewed", "हाल ही में देखे")) },
                    leadingIcon = if (showRecent) ({ Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }) else null)
            }
            Text(t(settings, "Saved on this device only.", "केवल इसी डिवाइस पर सहेजा गया।"), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }}
        if (sites.isEmpty()) item { EmptyCard(
            t(settings, if (showRecent) "No recent portals" else "No favorites yet",
                if (showRecent) "अभी कोई हाल का पोर्टल नहीं" else "अभी कोई पसंदीदा पोर्टल नहीं"),
            t(settings, if (showRecent) "Viewed portal details appear here." else "Tap the heart on a portal detail page to save it.",
                if (showRecent) "देखे गए पोर्टल यहाँ दिखेंगे।" else "पोर्टल को सहेजने के लिए उसके पेज पर दिल का चिह्न दबाएँ।"),
            if (onSearch != null) t(settings, "Find a portal", "पोर्टल ढूँढें") else null, onSearch, Illustration.NO_SAVED
        ) } else items(sites,key={it.id}) { PortalCard(it, settings, catalog.profileFor(it.id)) { onDetail(it.id) } }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(site: Website?, guide: Guide?, settings: LocalSettings, onBack: () -> Unit,
                 onOpen: () -> Unit, onSource: () -> Unit, onGuideSource: () -> Unit,
                 onReport: () -> Unit, categories: List<Category> = emptyList(),
                 profile: WebsiteProfile? = null, services: List<Service> = emptyList(),
                 onService: (String) -> Unit = {}, onLink: (String) -> Unit = {}) {
    LaunchedEffect(site?.id) { if (site != null) settings.recordViewed(site.id) }
    Page {
        Header(site?.title ?: t(settings, "Portal unavailable", "पोर्टल उपलब्ध नहीं"), settings, onBack = onBack)
        if (site == null) {
            EmptyCard(t(settings, "Portal unavailable", "पोर्टल उपलब्ध नहीं"),
                t(settings, "This portal is not in the current catalog.", "यह पोर्टल वर्तमान सूची में नहीं है।"),
                t(settings, "Go back", "वापस जाएँ"), onBack, Illustration.NO_RESULTS)
            return@Page
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            JanSetuStatusLabel(t(settings, "Website", "वेबसाइट"), StatusTone.NEUTRAL, Icons.Default.Language)
            if (site.isDirectoryListed) JanSetuStatusLabel(t(settings, "Listed by IGOD", "IGOD में सूचीबद्ध"), StatusTone.NEUTRAL, null)
            else JanSetuStatusLabel(t(settings, "Source verified", "स्रोत सत्यापित"), StatusTone.SUCCESS, Icons.Default.Verified)
            InformationStatusLabel(site, profile, settings)
            jurisdiction(site).takeIf { it.isNotEmpty() }?.let { JanSetuBadge(it, Icons.Default.Place) }
        }
        Text(t(settings,
            when {
                profile != null && site.isDirectoryListed -> "The record comes from the IGOD government directory. Its purpose, operator and help pages were then checked against the official website; a person has not reviewed it yet."
                site.isDirectoryListed -> "This record comes from the IGOD government directory. JanSetu has not manually reviewed its ownership or services."
                else -> "JanSetu manually reviewed this website record against its official source."
            },
            when {
                profile != null && site.isDirectoryListed -> "यह रिकॉर्ड IGOD सरकारी निर्देशिका से है। इसके उद्देश्य, संचालक और सहायता पेज आधिकारिक वेबसाइट से जाँचे गए; अभी किसी व्यक्ति ने समीक्षा नहीं की है।"
                site.isDirectoryListed -> "यह रिकॉर्ड IGOD सरकारी निर्देशिका से है। जनसेतु ने इसके स्वामित्व या सेवाओं की मैनुअल समीक्षा नहीं की है।"
                else -> "जनसेतु ने इस वेबसाइट रिकॉर्ड की आधिकारिक स्रोत से मैनुअल समीक्षा की है।"
            }),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        WebsitePurposeSection(site, profile, settings, portalDescription(site, settings))
        JanSetuOfficialLinkButton(
            t(settings, if (site.browserSupport == "in_app") "Open official website in JanSetu" else "Open official website",
                if (site.browserSupport == "in_app") "जनसेतु में आधिकारिक वेबसाइट खोलें" else "आधिकारिक वेबसाइट खोलें"),
            t(settings,
                if (site.browserSupport == "in_app") "Opens ${site.domain} in JanSetu's limited browser. Other sites and sign-in links may open in your browser. JanSetu never asks for your password or OTP."
                else "Opens ${site.domain} in your browser. JanSetu never asks for your portal password or OTP.",
                if (site.browserSupport == "in_app") "${site.domain} जनसेतु के सीमित ब्राउज़र में खुलेगा। दूसरी साइटें और साइन-इन लिंक आपके ब्राउज़र में खुल सकते हैं। जनसेतु आपका पासवर्ड या OTP नहीं माँगता।"
                else "${site.domain} आपके ब्राउज़र में खुलेगा। जनसेतु आपका पासवर्ड या OTP नहीं माँगता।"),
            onOpen)
        if (site.isDirectoryListed && site.hasInconclusiveLatestCheck) JanSetuNotice(t(settings,
            "The latest destination check on ${dateLabel(site.latestHealthCheckedAt,settings)} was unavailable or inconclusive. The last recorded response was on ${dateLabel(site.healthCheckedAt,settings)}. Directory listing and ownership review are separate.",
            "${site.latestHealthCheckedAt?.take(10) ?: "अज्ञात तिथि"} की नवीनतम जाँच उपलब्ध नहीं थी या अनिर्णायक थी। अंतिम दर्ज उत्तर ${site.healthCheckedAt?.take(10)} को था। निर्देशिका सूची और स्वामित्व समीक्षा अलग हैं।"),
            tone = StatusTone.WARNING, title = t(settings, "Availability not confirmed", "उपलब्धता की पुष्टि नहीं"))
        else if (site.isDirectoryListed) Text(t(settings,
            "Destination responded on ${dateLabel(site.healthCheckedAt,settings)}. This is a directory listing; it has not received a manual ownership or service review.",
            "${site.healthCheckedAt?.take(10)} को वेबसाइट ने उत्तर दिया। यह निर्देशिका की सूची है; स्वामित्व और सेवाओं की मैनुअल समीक्षा नहीं हुई है।"),
            style = MaterialTheme.typography.bodyMedium)
        if (!site.knownLimitations.isNullOrBlank()) {
            JanSetuNotice(site.knownLimitations, tone = StatusTone.WARNING, title = t(settings, "Known limitation", "ज्ञात सीमा"))
        }
        OutlinedButton(onClick = { settings.toggleFavorite(site.id) }, modifier = Modifier.fillMaxWidth()) {
            Icon(if (site.id in settings.favorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(t(settings, if (site.id in settings.favorites) "Remove favorite" else "Save to favorites",
                if (site.id in settings.favorites) "पसंदीदा से हटाएँ" else "पसंदीदा में सहेजें"))
        }
        WebsiteOperatorAndAudienceSections(site, profile, settings, categories, jurisdiction(site))
        WebsiteServicesSection(services, profile, settings, onService, onLink) {
            if (site.useCases.isNotEmpty()) {
                Text(t(settings, "What you can do", "आप क्या कर सकते हैं"), style = MaterialTheme.typography.titleSmall)
                site.useCases.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            }
        }
        WebsiteHelpSection(profile, settings, onLink)
        JanSetuSectionCard(t(settings, "JanSetu guide", "जनसेतु मार्गदर्शिका"), icon = Icons.AutoMirrored.Filled.HelpOutline) {
            if (guide == null) {
                Text(t(settings, "No verified step-by-step guide is available for this portal yet. Use the official site for current instructions.",
                    "इस पोर्टल के लिए अभी सत्यापित चरण-दर-चरण मार्गदर्शिका उपलब्ध नहीं है। मौजूदा निर्देशों के लिए आधिकारिक साइट देखें।"),
                    style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(if(settings.language=="hi") guide.titleHi ?: guide.title else guide.title, style = MaterialTheme.typography.titleMedium)
                val guideSteps=if(settings.language=="hi" && guide.stepsHi.isNotEmpty()) guide.stepsHi else guide.steps
                guideSteps.forEachIndexed { index, step -> NumberedStep(index + 1, step) }
                if (guide.note.isNotBlank()) Text(if(settings.language=="hi") guide.noteHi ?: guide.note else guide.note, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(t(settings, "Reviewed", "समीक्षा की गई") + " ${dateLabel(guide.reviewedOn,settings)}",
                    style = MaterialTheme.typography.labelMedium)
                TextButton(onClick = onGuideSource) { Text(t(settings, "Guide source ↗", "मार्गदर्शिका का स्रोत ↗")) }
                Text(t(settings,"Only the information shown here has been sourced. Check the official page for current documents, eligibility, fees and deadlines.","यहाँ दी जानकारी के स्रोत उपलब्ध हैं। वर्तमान दस्तावेज़, पात्रता, शुल्क और अंतिम तिथियाँ आधिकारिक पेज पर जाँचें।"),
                    style = MaterialTheme.typography.bodySmall)
                if(settings.language=="hi") Text("यह जनसेतु की हिन्दी व्याख्या है, आधिकारिक अनुवाद नहीं।", style = MaterialTheme.typography.bodySmall)
            }
        }
        WebsiteSourcesSection(site, profile, settings, onSource, onLink)
        OutlinedButton(onClick = onReport, modifier = Modifier.fillMaxWidth()) {
            Text(t(settings, "Report a problem", "समस्या की रिपोर्ट करें"))
        }
        Text(t(settings, "Opens a draft in your email app. Nothing is sent until you choose to send it.",
            "आपके ईमेल ऐप में मसौदा खुलेगा। आपके भेजने तक कुछ नहीं भेजा जाएगा।"),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(t(settings,
            "This is a JanSetu summary, not government guidance. Check the official site for current rules and steps.",
            "यह जनसेतु का सारांश है, सरकारी निर्देश नहीं। मौजूदा नियमों और चरणों के लिए आधिकारिक साइट देखें।"),
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun NumberedStep(number: Int, text: String) {
    Row(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}, verticalAlignment = Alignment.Top) {
        Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(26.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Text("$number", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SettingsGroup(title: String, icon: ImageVector, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) =
    JanSetuSectionCard(title, icon = icon, content = content)

@Composable
private fun RadioRow(label: String, selected: Boolean, tag: String, onSelect: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = JanSetuSpacing.touchTarget)
        .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect).testTag(tag),
        verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SwitchRow(label: String, description: String, checked: Boolean, tag: String, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = JanSetuSpacing.touchTarget)
        .toggleable(value = checked, role = Role.Switch, onValueChange = onChange).testTag(tag),
        verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = null)
    }
}

@Composable
fun SettingsScreen(settings: LocalSettings, catalog: Catalog?, onBack: () -> Unit,
                   onPrivacy: () -> Unit, onSupport: () -> Unit, onCatalog: (() -> Unit)? = null,
                   onApplications: (() -> Unit)? = null, onHelp: (() -> Unit)? = null,
                   notificationsAvailable: (() -> Boolean)? = null, onHistoryCleared: () -> Unit = {},
                   onDocuments: (() -> Unit)? = null) {
    var chooseState by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    var historyCleared by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var notificationsOn by remember { mutableStateOf(notificationsAvailable?.invoke()) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notificationsOn = notificationsAvailable?.invoke() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) notificationsOn = notificationsAvailable?.invoke() }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    fun openSystem(intent: Intent) {
        try { context.startActivity(intent) } catch (_: ActivityNotFoundException) {
            android.widget.Toast.makeText(context, t(settings, "This Android setting is not available on this device.", "यह Android सेटिंग इस डिवाइस पर उपलब्ध नहीं है।"), android.widget.Toast.LENGTH_LONG).show()
        }
    }
    Page(Modifier.testTag("settings-content")) {
        Header(t(settings, "Settings", "सेटिंग्स"), settings, onBack = onBack)
        SettingsGroup(t(settings, "Appearance", "रूप"), Icons.Default.Palette) {
            Column(Modifier.selectableGroup()) {
                listOf(
                    ThemeMode.SYSTEM to t(settings, "System default", "सिस्टम के अनुसार"),
                    ThemeMode.LIGHT to t(settings, "Light", "हल्का"),
                    ThemeMode.DARK to t(settings, "Dark", "गहरा")
                ).forEach { (mode, label) ->
                    RadioRow(label, settings.themeMode == mode, "theme:${mode.key}") { settings.updateThemeMode(mode) }
                }
            }
        }
        SettingsGroup(t(settings, "Language", "भाषा"), Icons.Default.Language) {
            Column(Modifier.selectableGroup()) {
                RadioRow("English", settings.language == "en", "language:en") { settings.updateLanguage("en") }
                RadioRow("हिन्दी", settings.language == "hi", "language:hi") { settings.updateLanguage("hi") }
            }
            Text(t(settings, "Hindi covers navigation, Home, Search, website pages, guides, Settings and help. Service details, My Applications and My Documents are currently in English. Hindi text is JanSetu's own explanation, not an official translation; official source names are kept as published.",
                "हिन्दी नेविगेशन, होम, खोज, वेबसाइट पेज, मार्गदर्शिकाएँ, सेटिंग्स और सहायता में उपलब्ध है। सेवा विवरण, मेरे आवेदन और मेरे दस्तावेज़ अभी अंग्रेज़ी में हैं। हिन्दी पाठ जनसेतु की व्याख्या है, आधिकारिक अनुवाद नहीं; आधिकारिक स्रोत के नाम प्रकाशित रूप में रखे गए हैं।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SettingsGroup(t(settings, "Motion and readability", "गति और पठनीयता"), Icons.Default.TextFields) {
            SwitchRow(t(settings, "Reduce motion", "गति कम करें"),
                t(settings, "Turns off screen fades and illustration movement. JanSetu also follows Android's Remove animations setting.",
                    "स्क्रीन के फ़ेड और चित्रों की हलचल बंद करता है। जनसेतु Android की एनिमेशन हटाएँ सेटिंग का भी पालन करता है।"),
                settings.reduceMotion, "setting:reduce-motion") { settings.updateReduceMotion(it) }
            SwitchRow(t(settings, "Larger text in JanSetu", "जनसेतु में बड़ा पाठ"),
                t(settings, "Makes JanSetu's text 15% larger than your Android font size, up to twice the normal size. Official websites are not affected.",
                    "जनसेतु का पाठ आपके Android फ़ॉन्ट आकार से 15% बड़ा करता है, सामान्य आकार के दोगुने तक। आधिकारिक वेबसाइटों पर असर नहीं पड़ता।"),
                settings.largerText, "setting:larger-text") { settings.updateLargerText(it) }
            TextButton(onClick = { openSystem(Intent(Settings.ACTION_DISPLAY_SETTINGS)) }) {
                Text(t(settings, "Android text size and display settings", "Android पाठ आकार और डिस्प्ले सेटिंग्स"))
            }
        }
        SettingsGroup(t(settings, "Your region", "आपका क्षेत्र"), Icons.Default.Place) {
            OutlinedButton(onClick = { chooseState = true }) { Text(settings.state) }
            Text(t(settings, "Used as the starting state filter in Search when the catalog lists that state. Coverage varies by region.",
                "सूची में वह राज्य होने पर खोज में शुरुआती राज्य फ़िल्टर के रूप में उपयोग होता है। क्षेत्र के अनुसार कवरेज अलग है।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (notificationsAvailable != null) SettingsGroup(t(settings, "Reminders and notifications", "रिमाइंडर और सूचनाएँ"), Icons.Default.Notifications) {
            if (notificationsOn == true) JanSetuStatusLabel(t(settings, "Notifications allowed", "सूचनाएँ अनुमति प्राप्त"), StatusTone.SUCCESS, null)
            else JanSetuStatusLabel(t(settings, "Notifications off", "सूचनाएँ बंद"), StatusTone.WARNING, null)
            Text(t(settings, "Reminders are optional and set separately on each application. JanSetu never turns one on for you. A reminder is a local notification near a date you entered; Android may delay it, and it is not a message from any government office.",
                "रिमाइंडर वैकल्पिक हैं और हर आवेदन पर अलग से चालू होते हैं। जनसेतु इन्हें अपने आप चालू नहीं करता। रिमाइंडर आपकी दर्ज तिथि के पास एक स्थानीय सूचना है; Android इसमें देरी कर सकता है और यह किसी सरकारी कार्यालय का संदेश नहीं है।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (notificationsOn != true) {
                val needsRuntime = Build.VERSION.SDK_INT >= 33 && androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
                if (needsRuntime) TextButton(onClick = { permission.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                    Text(t(settings, "Allow notifications", "सूचनाओं की अनुमति दें"))
                }
                Text(t(settings, "If you denied the permission earlier, Android may not ask again. Use notification settings instead.",
                    "यदि आपने पहले अनुमति नहीं दी थी, तो Android दोबारा नहीं पूछ सकता। इसके बजाय सूचना सेटिंग्स खोलें।"),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = { openSystem(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)) }) {
                Text(t(settings, "Notification settings", "सूचना सेटिंग्स"))
            }
        }
        SettingsGroup(t(settings, "Privacy and your data", "गोपनीयता और आपका डेटा"), Icons.Default.Lock) {
            Text(t(settings, "Your language, appearance, state, favorites and recent views stay on this device. The app has no account or analytics.",
                "आपकी भाषा, रूप, राज्य, पसंदीदा और हाल में देखे गए पेज इसी डिवाइस पर रहते हैं। ऐप में खाता या एनालिटिक्स नहीं हैं।"),
                style = MaterialTheme.typography.bodyMedium)
            Text(t(settings, "My Applications records are encrypted on this device and excluded from backup and transfer. There is no cloud copy: uninstalling JanSetu, clearing its storage or losing the device's security key removes them permanently.",
                "मेरे आवेदन के रिकॉर्ड इस डिवाइस पर एन्क्रिप्टेड हैं और बैकअप या स्थानांतरण में शामिल नहीं होते। कोई क्लाउड प्रति नहीं है: जनसेतु हटाने, इसका स्टोरेज साफ़ करने या डिवाइस की सुरक्षा कुंजी खोने पर ये स्थायी रूप से हट जाते हैं।"),
                style = MaterialTheme.typography.bodyMedium)
            if(onApplications!=null) OutlinedButton(onClick=onApplications,modifier=Modifier.fillMaxWidth()) {Text(t(settings,"My Applications · local profile","मेरे आवेदन · स्थानीय प्रोफ़ाइल"))}
            Text(t(settings, "My Documents are encrypted with their own separate key, excluded from backup and transfer, and never uploaded by JanSetu. Losing the key, uninstalling or clearing storage removes them permanently.",
                "मेरे दस्तावेज़ अपनी अलग कुंजी से एन्क्रिप्टेड हैं, बैकअप या स्थानांतरण में शामिल नहीं होते और जनसेतु इन्हें कभी अपलोड नहीं करता। कुंजी खोने, ऐप हटाने या स्टोरेज साफ़ करने पर ये स्थायी रूप से हट जाते हैं।"),
                style = MaterialTheme.typography.bodyMedium)
            if(onDocuments!=null) OutlinedButton(onClick=onDocuments,modifier=Modifier.fillMaxWidth().testTag("settings-documents")) {Text(t(settings,"My Documents","मेरे दस्तावेज़"))}
            OutlinedButton(onClick={confirmClear=true},modifier=Modifier.fillMaxWidth()) {Text(t(settings,"Clear search and viewing history","खोज और देखने का इतिहास मिटाएँ"))}
            JanSetuFeedbackNotice(if (historyCleared) t(settings, "Search and viewing history cleared.", "खोज और देखने का इतिहास मिटाया गया।") else null,
                t(settings, "Dismiss", "बंद करें"), { historyCleared = false })
            OutlinedButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth()) { Text(t(settings, "Privacy policy", "गोपनीयता नीति")) }
        }
        SettingsGroup(t(settings, "Offline catalog", "ऑफलाइन सूची"), Icons.Default.Storage) {
            Text(t(settings, "Version ${catalog?.version ?: "…"} · ${catalog?.websites?.size ?: 0} websites · ${catalog?.services?.size ?: 0} services. Search works without internet.",
                "संस्करण ${catalog?.version ?: "…"} · ${catalog?.websites?.size ?: 0} वेबसाइट · ${catalog?.services?.size ?: 0} सेवाएँ। खोज बिना इंटरनेट काम करती है।"),
                style = MaterialTheme.typography.bodyMedium)
            if(onCatalog!=null) OutlinedButton(onClick=onCatalog,modifier=Modifier.fillMaxWidth()) { Text(t(settings,"Offline catalog and updates","ऑफलाइन सूची और अपडेट")) }
        }
        SettingsGroup(t(settings, "Help and about", "सहायता और परिचय"), Icons.AutoMirrored.Filled.HelpOutline) {
            Text(t(settings,
                "JanSetu is an independent directory of government websites. It is not affiliated with or endorsed by the Government of India. The local catalog contains ${catalog?.websites?.size ?: 0} websites with their source attribution; coverage is incomplete.",
                "जनसेतु सरकारी वेबसाइटों की स्वतंत्र सूची है। यह भारत सरकार से संबद्ध या समर्थित नहीं है। स्थानीय सूची में स्रोत सहित ${catalog?.websites?.size ?: 0} वेबसाइट हैं; कवरेज अधूरा है।"),
                style = MaterialTheme.typography.bodyMedium)
            if (onHelp != null) OutlinedButton(onClick = onHelp, modifier = Modifier.fillMaxWidth()) { Text(t(settings, "Help & guides", "सहायता और मार्गदर्शिकाएँ")) }
            OutlinedButton(onClick = onSupport, modifier = Modifier.fillMaxWidth()) {
                Text(t(settings, "Contact support", "सहायता से संपर्क करें"))
            }
            val version = remember(context) { runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: "?" }
            Text(t(settings, "Version $version", "संस्करण $version"),
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if (chooseState) StateDialog(settings, onDismiss = { chooseState = false })
    if (confirmClear) JanSetuConfirmDialog(
        t(settings, "Clear history?", "इतिहास मिटाएँ?"),
        t(settings, "Recent searches and recently viewed portals will be removed from this device. Favorites, settings and My Applications are not affected.",
            "हाल की खोजें और हाल में देखे गए पोर्टल इस डिवाइस से हट जाएँगे। पसंदीदा, सेटिंग्स और मेरे आवेदन पर असर नहीं पड़ेगा।"),
        t(settings, "Clear history", "इतिहास मिटाएँ"), t(settings, "Cancel", "रद्द करें"),
        onConfirm = { settings.clearHistory(); confirmClear = false; historyCleared = true; onHistoryCleared() }, onDismiss = { confirmClear = false })
}

@Composable
fun PrivacyScreen(settings: LocalSettings, onBack: () -> Unit) {
    val context = LocalContext.current
    val policy = remember(context,settings.language) {
        context.assets.open(if(settings.language=="hi") "privacy-hi.txt" else "privacy.txt").bufferedReader().use { it.readText() }
    }
    Page {
        Header(t(settings, "Privacy policy", "गोपनीयता नीति"), settings, onBack = onBack)
        Text(policy, style = MaterialTheme.typography.bodyMedium)
    }
}

private val statesAndTerritories = listOf(
    "All India", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat",
    "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra",
    "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
    "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands",
    "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Jammu and Kashmir", "Ladakh",
    "Lakshadweep", "Puducherry"
)

@Composable
private fun StateDialog(settings: LocalSettings, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t(settings, "Choose a region", "क्षेत्र चुनें")) },
        text = {
            LazyColumn(Modifier.heightIn(max = 420.dp).selectableGroup()) {
                items(statesAndTerritories) { state ->
                    Row(Modifier.fillMaxWidth().heightIn(min = JanSetuSpacing.touchTarget)
                        .selectable(selected = settings.state == state, role = Role.RadioButton) {
                            settings.updateState(state)
                            onDismiss()
                        }, verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = settings.state == state, onClick = null)
                        Spacer(Modifier.width(10.dp))
                        Text(state, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(t(settings, "Close", "बंद करें")) } }
    )
}

@Composable
fun LoadingScreen(settings: LocalSettings) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            JanSetuLogoMark(size = 56.dp)
            CircularProgressIndicator()
            Text(t(settings, "Loading local catalog…", "स्थानीय सूची लोड हो रही है…"))
        }
    }
}

@Composable
fun ErrorScreen(settings: LocalSettings, message: String, onRetry: () -> Unit) {
    Page {
        Header(t(settings, "Catalog unavailable", "सूची उपलब्ध नहीं"), settings)
        EmptyCard(t(settings, "Could not load the catalog", "सूची लोड नहीं हुई"), message,
            t(settings, "Try again", "फिर कोशिश करें"), onRetry, Illustration.OFFLINE)
    }
}
