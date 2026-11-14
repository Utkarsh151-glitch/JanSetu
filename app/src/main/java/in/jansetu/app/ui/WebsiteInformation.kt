package `in`.jansetu.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import `in`.jansetu.app.catalog.Category
import `in`.jansetu.app.catalog.ProfileFact
import `in`.jansetu.app.catalog.ProfileLink
import `in`.jansetu.app.catalog.Service
import `in`.jansetu.app.catalog.Website
import `in`.jansetu.app.catalog.WebsiteProfile
import `in`.jansetu.app.settings.LocalSettings
import java.net.URI

/**
 * The website detail page in six questions: what the site is for, who runs it, who it
 * serves, which verified services and tasks it has, where official help is, and when and
 * where the information was checked. Each answer is a checked fact, a directory listing
 * clearly labelled as unchecked, or an honest "not checked yet". Nothing is inferred.
 */

/** "Purpose checked" for a published profile; "Information under review" for a directory-only record. */
@Composable
fun InformationStatusLabel(site: Website, profile: WebsiteProfile?, settings: LocalSettings) {
    when {
        profile?.purpose != null -> JanSetuStatusLabel(t(settings, "Purpose checked", "उद्देश्य जाँचा गया"), StatusTone.SUCCESS, Icons.Default.Verified)
        site.isDirectoryListed -> JanSetuStatusLabel(t(settings, "Information under review", "जानकारी समीक्षा में है"), StatusTone.NEUTRAL, Icons.Default.HourglassEmpty)
    }
}

private fun notCheckedYet(settings: LocalSettings) = t(settings, "Not checked yet", "अभी जाँचा नहीं गया")
private fun notStated(settings: LocalSettings) =
    t(settings, "Not stated on the official pages JanSetu checked", "जनसेतु द्वारा जाँचे गए आधिकारिक पेजों पर नहीं बताया गया")

/** A checked value, "not stated" (looked for, absent) or "not checked yet". The three are never merged. */
private fun factValue(fact: ProfileFact?, field: String, profile: WebsiteProfile?, settings: LocalSettings): String = when {
    fact != null -> fact.value
    profile != null && field in profile.notStated -> notStated(settings)
    else -> notCheckedYet(settings)
}

private fun host(url: String) = runCatching { URI(url).host }.getOrNull().orEmpty()

@Composable
private fun EnglishNote(settings: LocalSettings) {
    if (settings.language == "hi") Text("जाँची गई जानकारी अंग्रेज़ी में जनसेतु का सारांश है।", style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun LinkRow(label: String, caption: String, icon: ImageVector, tag: String, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = JanSetuLayout.chipShape, color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().heightIn(min = JanSetuSpacing.touchTarget).testTag(tag).semantics { role = Role.Button }) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleSmall)
                Text(caption, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** 1. What this website is for. */
@Composable
fun WebsitePurposeSection(site: Website, profile: WebsiteProfile?, settings: LocalSettings, directoryText: String) {
    JanSetuSectionCard(t(settings, "What this website is for", "यह वेबसाइट किस लिए है"), icon = Icons.Default.Info) {
        val purpose = profile?.purpose
        when {
            purpose != null -> {
                Text(purpose.value, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("website-purpose"))
                Text(t(settings, "Checked against the official website on ${dateLabel(profile.checkedOn, settings)}.",
                    "${dateLabel(profile.checkedOn, settings)} को आधिकारिक वेबसाइट से जाँचा गया।"),
                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                EnglishNote(settings)
            }
            !site.isDirectoryListed -> Text(directoryText, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.testTag("website-purpose"))
            else -> {
                Text(notCheckedYet(settings), style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("website-purpose-missing"))
                Text(t(settings, "JanSetu has not yet checked what this website is for. Directory listing: $directoryText",
                    "जनसेतु ने अभी नहीं जाँचा है कि यह वेबसाइट किस लिए है। निर्देशिका की सूची: $directoryText"),
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** 2. Department or organisation, and 3. who it serves and where. */
@Composable
fun WebsiteOperatorAndAudienceSections(site: Website, profile: WebsiteProfile?, settings: LocalSettings,
                                       categories: List<Category>, jurisdictionLabel: String) {
    JanSetuSectionCard(t(settings, "Department or organisation", "विभाग या संगठन"), icon = Icons.Default.AccountBalance) {
        when {
            profile?.operator != null -> {
                JanSetuFactRow(t(settings, "Run by (checked)", "संचालक (जाँचा गया)"), profile.operator.value)
                EnglishNote(settings)
            }
            !site.isDirectoryListed -> JanSetuFactRow(t(settings, "Run by", "संचालक"), site.organization)
            site.organization.trim().equals(site.title.trim(), ignoreCase = true) ->
                JanSetuFactRow(t(settings, "Run by", "संचालक"), t(settings, "Not checked yet. The directory gives no separate department name.",
                    "अभी जाँचा नहीं गया। निर्देशिका में अलग विभाग का नाम नहीं है।"))
            else -> JanSetuFactRow(t(settings, "Listed organization", "सूचीबद्ध संगठन"),
                site.organization + t(settings, " (from the IGOD directory, not checked)", " (IGOD निर्देशिका से, जाँचा नहीं गया)"))
        }
    }
    JanSetuSectionCard(t(settings, "Who it serves and where", "किसके लिए और कहाँ"), icon = Icons.Default.Groups) {
        JanSetuFactRow(t(settings, "Who it serves", "किसके लिए"), factValue(profile?.audience, "audience", profile, settings))
        JanSetuFactRow(t(settings, "Jurisdiction", "क्षेत्राधिकार"), when {
            profile?.jurisdiction != null -> profile.jurisdiction.value
            jurisdictionLabel.isNotEmpty() -> jurisdictionLabel + if (site.isDirectoryListed) t(settings, " (from the IGOD directory)", " (IGOD निर्देशिका से)") else ""
            else -> notCheckedYet(settings)
        })
        JanSetuFactRow(t(settings, "Languages", "भाषाएँ"), factValue(profile?.languages, "languages", profile, settings))
        JanSetuFactRow(t(settings, "Official domain", "आधिकारिक डोमेन"), site.domain)
        val names = site.categories.mapNotNull { id -> categories.firstOrNull { it.id == id } }
            .map { if (settings.language == "hi") it.titleHi else it.title }
        if (names.isNotEmpty()) JanSetuFactRow(t(settings, "Topics", "विषय"), names.joinToString(", "))
    }
}

/** 4. Verified services (separate service records) and official task pages. */
@Composable
fun WebsiteServicesSection(services: List<Service>, profile: WebsiteProfile?, settings: LocalSettings,
                           onService: (String) -> Unit, onLink: (String) -> Unit, extra: @Composable ColumnScope.() -> Unit = {}) {
    JanSetuSectionCard(t(settings, "Verified services and tasks", "सत्यापित सेवाएँ और कार्य"), icon = Icons.Default.TaskAlt) {
        if (services.isEmpty() && profile?.tasks.isNullOrEmpty()) {
            Text(t(settings, "No JanSetu-verified services or task pages are linked to this website yet. A website listing is not a service.",
                "इस वेबसाइट से अभी कोई जनसेतु-सत्यापित सेवा या कार्य पेज जुड़ा नहीं है। वेबसाइट की सूची कोई सेवा नहीं है।"),
                style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("website-services-missing"))
        }
        services.forEach { service ->
            LinkRow(service.title, t(settings, "Verified service · ${serviceCoverage(service, settings)}", "सत्यापित सेवा · ${serviceCoverage(service, settings)}"),
                Icons.AutoMirrored.Filled.ArrowForward, "website-service:${service.id}") { onService(service.id) }
        }
        profile?.tasks.orEmpty().forEach { task -> OfficialLinkRow(task, settings, "website-task", onLink) }
        if (!profile?.tasks.isNullOrEmpty()) Text(t(settings, "Task links open the official website. Sign-in, forms and payment happen there, not in JanSetu.",
            "कार्य लिंक आधिकारिक वेबसाइट खोलते हैं। साइन-इन, फ़ॉर्म और भुगतान वहीं होते हैं, जनसेतु में नहीं।"),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        extra()
    }
}

@Composable
private fun OfficialLinkRow(link: ProfileLink, settings: LocalSettings, tagPrefix: String, onLink: (String) -> Unit) {
    LinkRow(link.label, t(settings, "Official page · ${host(link.url)} ↗", "आधिकारिक पेज · ${host(link.url)} ↗"),
        Icons.AutoMirrored.Filled.OpenInNew, "$tagPrefix:${link.url}") { onLink(link.url) }
}

/** 5. Official help and contact pages (links only; phone numbers and emails are not copied). */
@Composable
fun WebsiteHelpSection(profile: WebsiteProfile?, settings: LocalSettings, onLink: (String) -> Unit) {
    JanSetuSectionCard(t(settings, "Official help and contact", "आधिकारिक सहायता और संपर्क"), icon = Icons.Default.SupportAgent) {
        val help = profile?.help.orEmpty()
        if (help.isEmpty()) {
            Text(if (profile != null && "help" in profile.notStated)
                t(settings, "No official help or contact page was found on the pages JanSetu checked. Use the official website.",
                    "जनसेतु द्वारा जाँचे गए पेजों पर कोई आधिकारिक सहायता या संपर्क पेज नहीं मिला। आधिकारिक वेबसाइट देखें।")
            else t(settings, "No official help or contact page has been checked yet. Use the official website.",
                "अभी कोई आधिकारिक सहायता या संपर्क पेज जाँचा नहीं गया है। आधिकारिक वेबसाइट देखें।"),
                style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("website-help-missing"))
        } else {
            help.forEach { OfficialLinkRow(it, settings, "website-help", onLink) }
            Text(t(settings, "JanSetu links to these pages and does not copy phone numbers or email addresses. Never share a password or OTP.",
                "जनसेतु इन पेजों का लिंक देता है, फ़ोन नंबर या ईमेल कॉपी नहीं करता। पासवर्ड या OTP कभी साझा न करें।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** 6. When it was checked and where the information came from. */
@Composable
fun WebsiteSourcesSection(site: Website, profile: WebsiteProfile?, settings: LocalSettings, onSource: () -> Unit, onLink: (String) -> Unit) {
    JanSetuSectionCard(t(settings, "When checked and where from", "कब और कहाँ से जाँचा गया"), icon = Icons.Default.History) {
        if (profile != null) {
            Text(if (profile.isHumanReviewed)
                t(settings, "Reviewed by a person against the official website on ${dateLabel(profile.reviewedOn, settings)}. Sources saved on ${dateLabel(profile.checkedOn, settings)}.",
                    "${dateLabel(profile.reviewedOn, settings)} को एक व्यक्ति ने आधिकारिक वेबसाइट से समीक्षा की। स्रोत ${dateLabel(profile.checkedOn, settings)} को सहेजे गए।")
            else t(settings, "Checked on ${dateLabel(profile.checkedOn, settings)} by JanSetu's automated check against saved copies of the official pages. A person has not reviewed it yet.",
                "${dateLabel(profile.checkedOn, settings)} को जनसेतु की स्वचालित जाँच ने आधिकारिक पेजों की सहेजी प्रतियों से जाँचा। अभी किसी व्यक्ति ने समीक्षा नहीं की है।"),
                style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("website-review-status"))
            JanSetuExpandableSection(t(settings, "Sources", "स्रोत"),
                t(settings, "${profile.evidence.size} official ${if (profile.evidence.size == 1) "statement" else "statements"} from ${profile.evidence.map { host(it.url) }.distinct().joinToString()}",
                    "${profile.evidence.map { host(it.url) }.distinct().joinToString()} से ${profile.evidence.size} आधिकारिक कथन"),
                t(settings, "Show sources", "स्रोत दिखाएँ"), t(settings, "Hide sources", "स्रोत छिपाएँ"), testTag = "website-sources") {
                profile.evidence.distinctBy { it.url to it.claim }.forEach { evidence ->
                    JanSetuSourceAttribution(evidence.claim, t(settings, "Checked", "जाँच") + " " + dateLabel(evidence.checkedOn, settings),
                        null, t(settings, "Open ${host(evidence.url)} ↗", "${host(evidence.url)} खोलें ↗")) { onLink(evidence.url) }
                }
            }
            if (profile.uncertainty.isNotEmpty()) JanSetuNotice(profile.uncertainty.joinToString("\n") { "• $it" }, tone = StatusTone.INFO,
                title = t(settings, "Notes from the check", "जाँच के नोट"))
        }
        JanSetuSourceAttribution(
            publisher = site.sourceType,
            checkedOn = t(settings, "Checked", "जाँच") + " " + dateLabel(if (site.isDirectoryListed) site.sourceRetrievedOn else site.verifiedOn, settings),
            detail = if (site.isDirectoryListed) t(settings, "The directory record says where the website is listed, not what it offers.",
                "निर्देशिका का रिकॉर्ड बताता है कि वेबसाइट कहाँ सूचीबद्ध है, यह नहीं कि उस पर क्या उपलब्ध है।") else null,
            linkLabel = t(settings, "View source page", "स्रोत पेज देखें"),
            onOpen = onSource)
    }
}
