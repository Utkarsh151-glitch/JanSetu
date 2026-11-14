package `in`.jansetu.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.jansetu.app.catalog.Catalog
import `in`.jansetu.app.settings.LocalSettings

/**
 * Plain-language help. Explains the labels used across the app and links to
 * the source-backed guides that actually exist in the catalog.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HelpScreen(settings: LocalSettings, catalog: Catalog?, onBack: () -> Unit, onGuide: (String) -> Unit,
               onSearch: () -> Unit, onPrivacy: () -> Unit, onSupport: () -> Unit) {
    JanSetuPage {
        JanSetuScreenHeader(t(settings, "Help & guides", "सहायता और मार्गदर्शिकाएँ"), backLabel = t(settings, "Back", "पीछे"), onBack = onBack)
        Row(verticalAlignment = Alignment.CenterVertically) {
            JanSetuIllustration(Illustration.HELP, Modifier.size(72.dp))
            Spacer(Modifier.width(14.dp))
            Text(t(settings, "JanSetu helps you find the right official website or service information. You then complete the task on that official website.",
                "जनसेतु सही आधिकारिक वेबसाइट या सेवा जानकारी खोजने में मदद करता है। फिर आप वह काम उसी आधिकारिक वेबसाइट पर पूरा करते हैं।"),
                style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        }
        JanSetuSectionCard(t(settings, "Finding what you need", "ज़रूरत की चीज़ खोजना"), icon = Icons.Default.Search) {
            NumberedStep(1, t(settings, "Search in your own words, such as “income certificate Kerala” or “find a job”. Small spelling mistakes are tolerated.",
                "अपने शब्दों में खोजें, जैसे “आय प्रमाण पत्र केरल” या “नौकरी खोजें”। छोटी वर्तनी गलतियाँ चल जाती हैं।"))
            NumberedStep(2, t(settings, "Use the filters to narrow by type, topic, level, state or district. “Clear filters” resets them.",
                "प्रकार, विषय, स्तर, राज्य या जिले के अनुसार फ़िल्टर करें। “फ़िल्टर हटाएँ” से सब रीसेट होते हैं।"))
            NumberedStep(3, t(settings, "Open a result to read what it is, where it applies and how it was checked, then continue to the official website.",
                "परिणाम खोलकर पढ़ें कि यह क्या है, कहाँ लागू है और कैसे जाँचा गया, फिर आधिकारिक वेबसाइट पर जाएँ।"))
            TextButton(onClick = onSearch) { Text(t(settings, "Start a search", "खोज शुरू करें")) }
        }
        JanSetuSectionCard(t(settings, "What the labels mean", "लेबल का अर्थ"), icon = Icons.Default.Info) {
            HelpLabel({ JanSetuStatusLabel(t(settings, "Verified service", "सत्यापित सेवा"), StatusTone.INFO, Icons.Default.Verified) },
                t(settings, "JanSetu checked this service's facts against official sources. Unknown details are marked as not verified.",
                    "जनसेतु ने इस सेवा के तथ्य आधिकारिक स्रोतों से जाँचे। अज्ञात विवरण “सत्यापित नहीं” के रूप में दिखते हैं।"))
            HelpLabel({ JanSetuStatusLabel(t(settings, "Website", "वेबसाइट"), StatusTone.NEUTRAL, Icons.Default.Language) },
                t(settings, "A government website. A listing does not mean a service is available or that you are eligible.",
                    "एक सरकारी वेबसाइट। सूची में होने का अर्थ यह नहीं कि सेवा उपलब्ध है या आप पात्र हैं।"))
            HelpLabel({ JanSetuStatusLabel(t(settings, "Listed by IGOD", "IGOD में सूचीबद्ध"), StatusTone.NEUTRAL, null) },
                t(settings, "Taken from the government's IGOD directory. JanSetu has not manually reviewed it.",
                    "सरकारी IGOD निर्देशिका से लिया गया। जनसेतु ने इसकी मैनुअल समीक्षा नहीं की।"))
            HelpLabel({ JanSetuStatusLabel(t(settings, "Apply online · official route confirmed", "ऑनलाइन आवेदन · आधिकारिक मार्ग की पुष्टि"), StatusTone.SUCCESS, Icons.Default.Language) },
                t(settings, "JanSetu confirmed the department's own online application entry. You apply there, not in JanSetu.",
                    "जनसेतु ने विभाग के अपने ऑनलाइन आवेदन पेज की पुष्टि की। आवेदन वहीं होता है, जनसेतु में नहीं।"))
            HelpLabel({ JanSetuStatusLabel(t(settings, "Apply with help at a service centre", "सेवा केंद्र की सहायता से आवेदन"), StatusTone.NEUTRAL, Icons.Default.Storefront) },
                t(settings, "The official process uses an assisted centre. Check the official page for the nearest option.",
                    "आधिकारिक प्रक्रिया में सहायता केंद्र का उपयोग होता है। निकटतम विकल्प आधिकारिक पेज पर देखें।"))
            HelpLabel({ JanSetuStatusLabel(t(settings, "Information only", "केवल जानकारी"), StatusTone.NEUTRAL, Icons.Default.Info) },
                t(settings, "JanSetu has information but no confirmed application route, so no Apply button is shown.",
                    "जनसेतु के पास जानकारी है पर आवेदन का पुष्ट मार्ग नहीं, इसलिए आवेदन बटन नहीं दिखता।"))
        }
        JanSetuSectionCard(t(settings, "Staying safe", "सुरक्षित रहें"), icon = Icons.Default.Security) {
            Text(t(settings, "JanSetu never asks for passwords, OTPs, Aadhaar numbers, bank details or identity documents. Enter those only on the official website, and check the address shown before you do.",
                "जनसेतु कभी पासवर्ड, OTP, आधार नंबर, बैंक विवरण या पहचान दस्तावेज़ नहीं माँगता। इन्हें केवल आधिकारिक वेबसाइट पर दर्ज करें और पहले दिखाया गया पता जाँचें।"),
                style = MaterialTheme.typography.bodyMedium)
            Text(t(settings, "JanSetu is independent. It is not a government app and cannot approve, speed up or check the status of an application.",
                "जनसेतु स्वतंत्र है। यह सरकारी ऐप नहीं है और किसी आवेदन को स्वीकृत, तेज़ या उसकी स्थिति जाँच नहीं सकता।"),
                style = MaterialTheme.typography.bodyMedium)
        }
        JanSetuSectionCard(t(settings, "My Applications", "मेरे आवेदन"), icon = Icons.AutoMirrored.Filled.Assignment) {
            Text(t(settings, "A private notebook for applications you make on official websites. The status is what you record; it is not checked with any department. Records stay encrypted on this device and are lost if JanSetu is uninstalled.",
                "आधिकारिक वेबसाइटों पर किए आवेदनों के लिए निजी नोटबुक। स्थिति वही है जो आप दर्ज करते हैं; किसी विभाग से इसकी जाँच नहीं होती। रिकॉर्ड इस डिवाइस पर एन्क्रिप्टेड रहते हैं और जनसेतु हटाने पर मिट जाते हैं।"),
                style = MaterialTheme.typography.bodyMedium)
        }
        val guides = catalog?.guides.orEmpty().mapNotNull { guide -> catalog?.websites?.firstOrNull { it.id == guide.websiteId }?.let { guide to it } }
        JanSetuSectionTitle(t(settings, "Step-by-step guides", "चरण-दर-चरण मार्गदर्शिकाएँ"))
        if (guides.isEmpty()) JanSetuEmptyState(t(settings, "No guides available", "कोई मार्गदर्शिका उपलब्ध नहीं"),
            t(settings, "Guides appear here when the catalog includes a source-backed guide.", "सूची में स्रोत-आधारित मार्गदर्शिका होने पर यहाँ दिखेगी।"))
        guides.forEach { (guide, site) ->
            OutlinedCard(onClick = { onGuide(site.id) }, modifier = Modifier.fillMaxWidth(), shape = JanSetuLayout.cardShape) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(if (settings.language == "hi") guide.titleHi ?: guide.title else guide.title, style = MaterialTheme.typography.titleSmall)
                        Text(t(settings, "${site.title} · reviewed ${dateLabel(guide.reviewedOn, settings)}", "${site.title} · समीक्षा ${dateLabel(guide.reviewedOn, settings)}"),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        JanSetuSectionCard(t(settings, "Privacy and support", "गोपनीयता और सहायता"), icon = Icons.Default.Lock) {
            OutlinedButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth()) { Text(t(settings, "Read the privacy policy", "गोपनीयता नीति पढ़ें")) }
            OutlinedButton(onClick = onSupport, modifier = Modifier.fillMaxWidth()) { Text(t(settings, "Email JanSetu support", "जनसेतु सहायता को ईमेल करें")) }
            Text(t(settings, "Support opens a draft in your email app. Nothing is sent until you choose to send it.",
                "सहायता आपके ईमेल ऐप में मसौदा खोलती है। आपके भेजने तक कुछ नहीं भेजा जाता।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HelpLabel(label: @Composable () -> Unit, meaning: String) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        label()
        Text(meaning, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
