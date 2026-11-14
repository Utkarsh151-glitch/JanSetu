package `in`.jansetu.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import `in`.jansetu.app.catalog.Catalog
import `in`.jansetu.app.settings.LocalSettings

@Composable
fun OrganizationsScreen(catalog: Catalog, settings: LocalSettings, onBack: () -> Unit, onDetail: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val groups=remember(catalog) { catalog.websites.groupBy { it.organization }.toSortedMap() }
    val names=remember(groups,query) { groups.keys.filter { it.contains(query,ignoreCase=true) } }
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            JanSetuScreenHeader(selected ?: t(settings,"Organizations","संगठन"), backLabel=t(settings,"Back","पीछे"),
                onBack={if(selected!=null) selected=null else onBack()})
        }
        item {
            Text(t(settings,"Names are grouped exactly as listed. Similar names are not automatically merged.","दिए गए नामों के अनुसार समूह हैं। मिलते-जुलते नाम स्वतः नहीं जोड़े जाते।"),
                style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if(selected==null) {
            item {OutlinedTextField(value=query,onValueChange={query=it},label={Text(t(settings,"Find an organization","संगठन खोजें"))},
                leadingIcon={Icon(Icons.Default.Search,contentDescription=null)},singleLine=true,modifier=Modifier.fillMaxWidth())}
            if(names.isEmpty()) item {JanSetuEmptyState(t(settings,"No matching organizations","कोई संगठन नहीं मिला"),
                t(settings,"Try a shorter part of the name.","नाम का छोटा हिस्सा आज़माएँ।"),illustration=Illustration.NO_RESULTS)}
            items(names,key={it}) {name -> OutlinedCard(onClick={selected=name},modifier=Modifier.fillMaxWidth().testTag("organization:$name"),shape=JanSetuLayout.cardShape) {
                Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(name,style=MaterialTheme.typography.titleSmall)
                        Text(t(settings,"${groups[name]?.size} websites","${groups[name]?.size} वेबसाइट"),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward,contentDescription=null,tint=MaterialTheme.colorScheme.primary)
                }
            }}
        } else {
            items(groups[selected].orEmpty(),key={it.id}) {site -> OutlinedCard(onClick={onDetail(site.id)},modifier=Modifier.fillMaxWidth(),shape=JanSetuLayout.cardShape) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(2.dp)) {
                    Text(site.title,style=MaterialTheme.typography.titleSmall)
                    Text(site.domain,style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
                }
            }}
        }
    }
}
