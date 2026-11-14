package `in`.jansetu.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.jansetu.app.catalog.*
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.tracker.hasConfirmedApplicationRoute
import `in`.jansetu.app.catalog.citizenJurisdictionLevel
import java.net.URI

/** Plain-language application mode. Never offers more than the record confirms. */
@Composable fun ApplicationModeLabel(service: Service, settings: LocalSettings) {
    when {
        service.hasConfirmedApplicationRoute() -> JanSetuStatusLabel(t(settings, "Apply online · official route confirmed", "ऑनलाइन आवेदन · आधिकारिक मार्ग की पुष्टि"), StatusTone.SUCCESS, Icons.Default.Language)
        service.applicationMode == "online" -> JanSetuStatusLabel(t(settings, "Online portal · route not confirmed", "ऑनलाइन पोर्टल · मार्ग की पुष्टि नहीं"), StatusTone.NEUTRAL, Icons.Default.Language)
        service.applicationMode == "assisted" -> JanSetuStatusLabel(t(settings, "Apply with help at a service centre", "सेवा केंद्र की सहायता से आवेदन"), StatusTone.NEUTRAL, Icons.Default.Storefront)
        else -> JanSetuStatusLabel(t(settings, "Information only", "केवल जानकारी"), StatusTone.NEUTRAL, Icons.Default.Info)
    }
}

/** States the area a service covers so a state service is not read as nationwide. */
fun serviceCoverage(service: Service, settings: LocalSettings): String {
    val level = service.citizenJurisdictionLevel()
    return when {
        service.district != null -> t(settings, "${service.district}, ${service.state} only", "केवल ${service.district}, ${service.state}")
        service.state != null -> t(settings, "${service.state} only · $level", "केवल ${service.state} · $level")
        else -> t(settings, "Central government service", "केंद्र सरकार की सेवा")
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun ServiceResultCard(service:Service,settings:LocalSettings,onOpen:()->Unit) {
    Card(onClick=onOpen,modifier=Modifier.fillMaxWidth().testTag("service:${service.id}"),
        shape=JanSetuLayout.cardShape, colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerLow),
        border=BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                JanSetuStatusLabel(t(settings,"Verified service","सत्यापित सेवा"),StatusTone.INFO,Icons.Default.Verified)
                ApplicationModeLabel(service,settings)
            }
            Text(service.title,style=MaterialTheme.typography.titleLarge)
            JanSetuBadge(serviceCoverage(service,settings),Icons.Default.Place,emphasized=true)
            Text(service.description,style=MaterialTheme.typography.bodyMedium,maxLines=3,overflow=TextOverflow.Ellipsis,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text(t(settings,"Source checked ${service.verifiedOn} · Eligibility must be confirmed","स्रोत जाँच ${service.verifiedOn} · पात्रता की पुष्टि आवश्यक है"),
                style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun host(url: String) = runCatching { URI(url).host }.getOrNull() ?: url

@OptIn(ExperimentalLayoutApi::class)
@Composable fun ServiceScreen(service:Service?,settings:LocalSettings,onBack:()->Unit,onWebsite:()->Unit,onLink:(String)->Unit,onAddApplication:(()->Unit)?=null,
    onPrepare:(()->Unit)?=null,related:List<Service> = emptyList()) {
    JanSetuPage(verticalArrangement=Arrangement.spacedBy(16.dp)) {
        JanSetuScreenHeader(t(settings,"Service information","सेवा जानकारी"),backLabel=t(settings,"Back","पीछे"),onBack=onBack)
        if(service==null) {
            JanSetuEmptyState(t(settings,"Service unavailable","सेवा उपलब्ध नहीं"),t(settings,"This service is not in the current catalog.","यह सेवा वर्तमान सूची में उपलब्ध नहीं है।"),
                t(settings,"Go back","वापस जाएँ"),onBack,Illustration.NO_RESULTS)
            return@JanSetuPage
        }
        JanSetuStatusLabel(t(settings,"Verified service information","सत्यापित सेवा जानकारी"),StatusTone.INFO,Icons.Default.Verified)
        Text(service.title,style=MaterialTheme.typography.headlineMedium,modifier=Modifier.semantics {heading()})
        FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            JanSetuBadge(serviceCoverage(service,settings),Icons.Default.Place,emphasized=true)
            ApplicationModeLabel(service,settings)
        }
        Text(service.description,style=MaterialTheme.typography.bodyLarge)
        JanSetuFactRow(t(settings,"Department","विभाग"),service.department)
        JanSetuNotice(t(settings,"JanSetu guidance, checked ${service.verifiedOn}. Confirm current rules with the official department. This is not a guarantee of eligibility, approval, a live vacancy or processing time.","जनसेतु मार्गदर्शन, जाँच ${service.verifiedOn}। वर्तमान नियम आधिकारिक विभाग से जाँचें। यह पात्रता, स्वीकृति, रिक्ति या समय-सीमा की गारंटी नहीं है।"))
        BeforeYouRequestCard(BeforeYouRequest.items(service,related),settings,onLink)
        JanSetuSectionCard(t(settings,"Official actions","आधिकारिक कार्रवाई"),icon=Icons.AutoMirrored.Filled.OpenInNew) {
            JanSetuOfficialLinkButton(t(settings,"Official service information","आधिकारिक सेवा जानकारी"),
                t(settings,"Opens ${host(service.informationUrl)} in your browser.","${host(service.informationUrl)} आपके ब्राउज़र में खुलेगा।"),
                {onLink(service.informationUrl)},buttonTag="service-information")
            if(service.hasConfirmedApplicationRoute()) JanSetuOfficialLinkButton(t(settings,"Open official application route","आधिकारिक आवेदन पोर्टल खोलें"),
                t(settings,"Opens the department's own application entry at ${host(requireNotNull(service.applicationUrl))}. Sign-in, forms and payment happen there, not in JanSetu.",
                    "${host(requireNotNull(service.applicationUrl))} पर विभाग का अपना आवेदन पेज खुलेगा। साइन-इन, फ़ॉर्म और भुगतान वहीं होते हैं, जनसेतु में नहीं।"),
                {onLink(requireNotNull(service.applicationUrl))},primary=false,buttonTag="service-apply")
            else Text(t(settings,"A direct online application route has not been confirmed. This entry may require an assisted service centre; use the official information page.","सीधे ऑनलाइन आवेदन का मार्ग सत्यापित नहीं है। सेवा केंद्र की सहायता आवश्यक हो सकती है; आधिकारिक जानकारी देखें।"),
                style=MaterialTheme.typography.bodyMedium)
            if(service.hasConfirmedApplicationRoute() && onAddApplication!=null) {
                HorizontalDivider()
                OutlinedButton(onClick=onAddApplication,modifier=Modifier.fillMaxWidth().testTag("service-track")) {
                    Icon(Icons.AutoMirrored.Filled.Assignment,contentDescription=null,modifier=Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(t(settings,"Add to My Applications","मेरे आवेदनों में जोड़ें"))
                }
                Text(t(settings,"Create a local record of your own progress. This does not submit an application.","अपनी प्रगति का स्थानीय रिकॉर्ड बनाएँ। इससे आवेदन जमा नहीं होता।"),style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        val unknown=t(settings,"Not verified in this pilot. Check the official information page.","इस पायलट में सत्यापित नहीं। आधिकारिक जानकारी पेज देखें।")
        @Composable fun sourceLinks(fact:ServiceFact) {
            fact.evidenceIds.forEach { id -> service.evidence.firstOrNull { it.id==id }?.let { source ->
                TextButton(onClick={onLink(source.url)},contentPadding=PaddingValues(horizontal=4.dp)) {
                    Text(t(settings,"Source: ${source.publisher}","स्रोत: ${source.publisher}"),style=MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.OpenInNew,contentDescription=null,modifier=Modifier.size(14.dp))
                }
            }}
        }
        @Composable fun section(en:String,hi:String,icon:ImageVector,facts:List<ServiceFact>?,numbered:Boolean=false) {
            JanSetuSectionCard(t(settings,en,hi),icon=icon) {
                if(facts==null) Text(unknown,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                facts?.forEachIndexed { index, fact ->
                    if(numbered) NumberedStep(index+1,fact.text) else Text(fact.text,style=MaterialTheme.typography.bodyMedium)
                    sourceLinks(fact)
                }
            }
        }
        section("Who may apply","कौन आवेदन कर सकता है",Icons.Default.Groups,service.eligibility?.let {listOf(it)})
        if(service.beneficiaryGroups!=null) section("Documented applicant groups","स्रोत में बताए गए आवेदक समूह",Icons.Default.Groups,service.beneficiaryGroups)
        section("Documents required","आवश्यक दस्तावेज़",Icons.Default.Description,service.documents)
        section("Conditional documents","शर्त के अनुसार दस्तावेज़",Icons.Default.Badge,service.conditionalDocuments)
        Text(t(settings,"Listed documents may be partial; only the cited facts have been checked. Do not send identity documents or portal passwords to JanSetu support.","दस्तावेज़ों की सूची अधूरी हो सकती है; केवल उद्धृत तथ्यों की जाँच हुई है। पहचान दस्तावेज़ या पोर्टल पासवर्ड जनसेतु सहायता को न भेजें।"),
            style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        if(onPrepare!=null && `in`.jansetu.app.vault.Preparation.requirements(service).hasAny) {
            OutlinedButton(onClick=onPrepare,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp).testTag("service-prepare")) {
                Icon(Icons.Default.Description,contentDescription=null,modifier=Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(t(settings,"Prepare documents","दस्तावेज़ तैयार करें"))
            }
            Text(t(settings,"A private checklist of the documents listed above. You choose your own saved files; nothing is uploaded or checked.","ऊपर दिए दस्तावेज़ों की निजी सूची। आप अपनी सहेजी फ़ाइलें स्वयं चुनते हैं; कुछ भी अपलोड या जाँचा नहीं जाता।"),
                style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        section("How to apply","आवेदन प्रक्रिया",Icons.AutoMirrored.Filled.ListAlt,service.procedure,numbered=true)
        section("Fees","शुल्क",Icons.Default.Payments,service.fees?.let {listOf(it)})
        section("Deadline","अंतिम तिथि",Icons.Default.Event,service.deadline?.let {listOf(it)})
        section("Processing information","प्रक्रिया की जानकारी",Icons.Default.Schedule,service.processing?.let {listOf(it)})
        TextButton(onClick=onWebsite) {Text(t(settings,"Related website overview","संबंधित वेबसाइट का परिचय"))}
        JanSetuExpandableSection(t(settings,"Evidence and review","स्रोत और समीक्षा"),
            t(settings,"${service.evidence.size} official source(s) · checked ${service.verifiedOn}","${service.evidence.size} आधिकारिक स्रोत · जाँच ${service.verifiedOn}"),
            t(settings,"Show sources","स्रोत दिखाएँ"),t(settings,"Hide sources","स्रोत छिपाएँ"),testTag="service-evidence") {
            Text(service.reviewNotes,style=MaterialTheme.typography.bodyMedium)
            service.evidence.forEach { e ->
                HorizontalDivider()
                JanSetuSourceAttribution(e.publisher,t(settings,"Checked ${e.checkedOn}","जाँच ${e.checkedOn}"),"${e.summary}\n${e.locator}",host(e.url)) {onLink(e.url)}
            }
            service.applicationRouteEvidence?.let {Text(it.notes,style=MaterialTheme.typography.bodySmall)}
        }
        Text(t(settings,"Service content is currently in English. Hindi controls do not translate the official form.","सेवा का विवरण अभी अंग्रेज़ी में है। हिन्दी नियंत्रण आधिकारिक फॉर्म का अनुवाद नहीं करते।"),style=MaterialTheme.typography.bodySmall,
            color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Shown when a tracker shortcut is requested for a service without a confirmed online route. */
@Composable fun ServiceUnavailableForTracking(onBack:()->Unit) {
    JanSetuPage {
        JanSetuScreenHeader("Add application",onBack=onBack)
        JanSetuEmptyState("No confirmed online route",
            "This service does not currently have a confirmed online route. Add a manual record from My Applications if needed.",
            "Go back",onBack,Illustration.NO_APPLICATIONS)
    }
}
