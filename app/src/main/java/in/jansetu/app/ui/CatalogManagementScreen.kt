package `in`.jansetu.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import `in`.jansetu.app.catalog.Catalog
import `in`.jansetu.app.settings.LocalSettings

@Composable
fun CatalogManagementScreen(catalog: Catalog?, settings: LocalSettings, source: String, updating: Boolean,
    message: String?, recovered: Boolean, onBack: () -> Unit, onImport: () -> Unit, onRestore: () -> Unit) {
    var confirmRestore by remember { mutableStateOf(false) }
    JanSetuPage(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        JanSetuScreenHeader(t(settings,"Offline catalog","ऑफलाइन सूची"), backLabel = t(settings,"Back","पीछे"), onBack = onBack)
        JanSetuSectionCard(t(settings,"Catalog on this device","इस डिवाइस पर सूची"), icon = Icons.Default.Storage) {
            JanSetuFactRow(t(settings,"Version","संस्करण"), "${catalog?.version ?: "…"}")
            JanSetuFactRow(t(settings,"Contents","सामग्री"), t(settings,"${catalog?.websites?.size ?: 0} websites · ${catalog?.services?.size ?: 0} services",
                "${catalog?.websites?.size ?: 0} वेबसाइट · ${catalog?.services?.size ?: 0} सेवाएँ"))
            JanSetuFactRow(t(settings,"Source","स्रोत"), if(source=="local_update") t(settings,"Using an imported local update.","आयात किया स्थानीय अपडेट उपयोग में है।") else t(settings,"Using the catalog included with this app.","ऐप के साथ मिली सूची उपयोग में है।"))
            Text(t(settings,"Search, saved portals and guides work without a connection. Official websites require internet access.","खोज, सहेजे पोर्टल और मार्गदर्शिकाएँ बिना इंटरनेट उपलब्ध हैं। आधिकारिक वेबसाइटों के लिए इंटरनेट चाहिए।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if(recovered) JanSetuNotice(t(settings,"The saved update could not be read. The bundled catalog was recovered automatically.","सहेजा अपडेट पढ़ा नहीं जा सका। ऐप की मूल सूची स्वतः बहाल हुई।"), tone = StatusTone.WARNING)
        if(updating) JanSetuLoading(t(settings,"Checking the catalog file…","सूची फ़ाइल की जाँच हो रही है…"))
        if(message!=null) JanSetuNotice(message, Modifier.testTag("catalog-message"))
        JanSetuSectionCard(t(settings,"Update from a file","फ़ाइल से अपडेट"), icon = Icons.Default.FileOpen) {
            Text(t(settings,"Choose a newer signed JanSetu catalog file from this device. A file is checked before replacing your catalog. This local build has no automatic downloads.","इस डिवाइस से जनसेतु की नई हस्ताक्षरित सूची फ़ाइल चुनें। बदलने से पहले फ़ाइल जाँची जाती है। इस स्थानीय ऐप में स्वतः डाउनलोड नहीं है।"),
                style = MaterialTheme.typography.bodyMedium)
            Button(onClick=onImport,enabled=!updating && catalog!=null,modifier=Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text(t(settings,"Import catalog update","सूची अपडेट आयात करें")) }
        }
        JanSetuSectionCard(t(settings,"Restore","बहाली"), icon = Icons.Default.Restore) {
            Text(t(settings,"Restoring preserves your favorites and preferences. Portals absent from a catalog are hidden until they are available again.","बहाली से पसंदीदा पोर्टल और प्राथमिकताएँ सुरक्षित रहती हैं। सूची में अनुपलब्ध पोर्टल दोबारा उपलब्ध होने तक नहीं दिखते।"),
                style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick={confirmRestore=true},enabled=!updating && (source=="local_update" || recovered),modifier=Modifier.fillMaxWidth()) { Text(t(settings,"Restore bundled catalog","ऐप की मूल सूची बहाल करें")) }
            if(!(source=="local_update" || recovered)) Text(t(settings,"Available after importing an update. The bundled catalog is already in use.","अपडेट आयात करने के बाद उपलब्ध। ऐप की मूल सूची पहले से उपयोग में है।"),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if(confirmRestore) JanSetuConfirmDialog(t(settings,"Restore bundled catalog?","मूल सूची बहाल करें?"),
        t(settings,"The imported update will be removed from this device.","आयात किया अपडेट इस डिवाइस से हटेगा।"),
        t(settings,"Restore","बहाल करें"), t(settings,"Cancel","रद्द करें"),
        onConfirm = { confirmRestore=false;onRestore() }, onDismiss = { confirmRestore=false })
}
