package `in`.jansetu.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import `in`.jansetu.app.catalog.Service
import `in`.jansetu.app.catalog.ServiceFact
import `in`.jansetu.app.settings.LocalSettings

/**
 * "Before you request": JanSetu's own summary of the RTI Online request guidelines, which phones show at about
 * 0.4 scale (Phase 6A, page rti-guidelines). Owner-authorised in Phase 17. Every item is an existing, evidence-backed
 * service fact; nothing here is new guidance. The official page's acknowledgement checkbox, Submit and Cancel stay
 * entirely on the official site.
 */
object BeforeYouRequest {
    const val GUIDELINES = "https://rtionline.gov.in/guidelines.php?request"
    private const val REQUEST = "rti-central-request"
    private const val FIRST_APPEAL = "rti-first-appeal"

    data class Item(val heading: String, val headingHi: String, val fact: ServiceFact, val service: Service)

    /** The five rules, or nothing when this service is not the RTI request or a fact is missing from the record. */
    fun items(service: Service, services: List<Service>): List<Item> {
        if (service.id != REQUEST || service.informationUrl != GUIDELINES) return emptyList()
        val appeal = services.firstOrNull { it.id == FIRST_APPEAL && it.informationUrl == GUIDELINES }
        fun find(facts: List<ServiceFact>?, vararg words: String) = facts?.firstOrNull { f -> words.all { f.text.contains(it, ignoreCase = true) } }
        return listOfNotNull(
            find(service.conditionalDocuments, "poverty line", "certificate")?.let { Item("If you are below the poverty line", "यदि आप गरीबी रेखा से नीचे हैं", it, service) },
            find(service.procedure, "Aadhaar", "PAN")?.let { Item("Identity documents", "पहचान दस्तावेज़", it, service) },
            find(listOfNotNull(service.fees), "UPI")?.let { Item("Paying the fee", "शुल्क भुगतान", it, service) },
            find(service.procedure, "24–48", "do not pay again")?.let { Item("If no registration number appears after payment", "भुगतान के बाद पंजीकरण संख्या न मिले तो", it, service) },
            find(listOfNotNull(appeal?.fees), "first appeal")?.let { Item("If you later appeal", "यदि आप बाद में अपील करें", it, requireNotNull(appeal)) },
        )
    }
}

@Composable fun BeforeYouRequestCard(items: List<BeforeYouRequest.Item>, settings: LocalSettings, onLink: (String) -> Unit) {
    if (items.isEmpty()) return
    JanSetuSectionCard(t(settings, "Before you request", "अनुरोध से पहले"), Modifier.testTag("rti-before-request"), icon = Icons.Default.Checklist) {
        Text(t(settings, "Key rules from the official RTI Online guidelines, which are hard to read on a phone. Read the full guidelines before you start.",
            "आधिकारिक RTI Online दिशानिर्देशों के मुख्य नियम, जो फ़ोन पर पढ़ने में कठिन हैं। शुरू करने से पहले पूरे दिशानिर्देश पढ़ें।"),
            style = MaterialTheme.typography.bodyMedium)
        items.forEach { item ->
            HorizontalDivider()
            Column(Modifier.fillMaxWidth().testTag("rti-rule"), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(t(settings, item.heading, item.headingHi), style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
                Text(item.fact.text, style = MaterialTheme.typography.bodyMedium)
                item.fact.evidenceIds.mapNotNull { id -> item.service.evidence.firstOrNull { it.id == id } }.forEach { source ->
                    TextButton(onClick = { onLink(source.url) }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                        Text(t(settings, "Source: ${source.publisher} · checked ${source.checkedOn}", "स्रोत: ${source.publisher} · जाँच ${source.checkedOn}"),
                            style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
        HorizontalDivider()
        Text(t(settings, "These rules are for requests to central public authorities through RTI Online. State public authorities follow their own RTI rules.",
            "ये नियम RTI Online के माध्यम से केंद्रीय लोक प्राधिकरणों को अनुरोध के लिए हैं। राज्य के लोक प्राधिकरणों के अपने RTI नियम हैं।"),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(t(settings, "On RTI Online you read the guidelines, tick the acknowledgement yourself, and then choose Submit or Cancel. JanSetu does not tick it, submit, pay for or track your request, and does not check your documents.",
            "RTI Online पर आप दिशानिर्देश पढ़ते हैं, स्वीकृति पर स्वयं टिक करते हैं और फिर Submit या Cancel चुनते हैं। जनसेतु न टिक करता है, न अनुरोध जमा करता है, न भुगतान या ट्रैक करता है, और आपके दस्तावेज़ नहीं जाँचता।"),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.testTag("rti-boundary"))
        JanSetuOfficialLinkButton(t(settings, "Read the official guidelines", "आधिकारिक दिशानिर्देश पढ़ें"),
            t(settings, "Opens rtionline.gov.in in your browser.", "rtionline.gov.in आपके ब्राउज़र में खुलेगा।"),
            { onLink(BeforeYouRequest.GUIDELINES) }, primary = false, buttonTag = "rti-guidelines")
    }
}
