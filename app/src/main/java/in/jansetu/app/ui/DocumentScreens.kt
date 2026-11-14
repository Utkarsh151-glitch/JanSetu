package `in`.jansetu.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.activity.compose.LocalActivity
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import `in`.jansetu.app.catalog.Service
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.tracker.hasConfirmedApplicationRoute
import `in`.jansetu.app.vault.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

internal const val DOCUMENTS_PRIVACY = "Stored only on this device, encrypted with a key kept in Android's secure key storage. No account, cloud copy, backup or device transfer. Uninstalling JanSetu, clearing its storage or losing that key removes the documents permanently. Anyone using your unlocked phone can open JanSetu, so keep a screen lock."
private const val DOCUMENTS_STORAGE_ERROR = "My Documents could not be read or saved. Nothing has been deleted. Try again."
private const val NOT_CHECKED = "JanSetu has not checked whether this file meets the requirement."

/**
 * Hides sensitive screens from screenshots, screen recording and the recent-apps preview while any of them is
 * shown. Counted, so moving between two protected screens never drops the protection in between.
 */
@Composable internal fun SecureWindow() {
    val activity = LocalActivity.current ?: return
    DisposableEffect(activity) {
        if (SecureWindowCount.count++ == 0) activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose { if (--SecureWindowCount.count == 0) activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }
}
internal object SecureWindowCount { var count = 0 }

private fun formatInstant(value: String): String = runCatching {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()).format(Instant.parse(value).atZone(ZoneId.systemDefault()))
}.getOrDefault(value)

private fun typeIcon(type: DocumentType) = if (type == DocumentType.PDF) Icons.Default.PictureAsPdf else Icons.Default.Image

@Composable private fun DocumentMenuChip(label: String, tag: String, options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        AssistChip(onClick = { expanded = true }, modifier = Modifier.testTag(tag), label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp)) })
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { expanded = false; onSelect(index) },
                    leadingIcon = if (index == selectedIndex) ({ Icon(Icons.Default.Check, contentDescription = null) }) else null)
            }
        }
    }
}

/** What the system picker reported about a chosen file. The URI is used once for reading and never stored. */
private data class PickedFile(val uri: String, val name: String?, val mime: String?, val size: Long?)

private fun describe(context: Context, uri: Uri): PickedFile {
    var name: String? = null; var size: Long? = null
    runCatching {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                name = c.getColumnIndex(OpenableColumns.DISPLAY_NAME).takeIf { it >= 0 && !c.isNull(it) }?.let(c::getString)
                size = c.getColumnIndex(OpenableColumns.SIZE).takeIf { it >= 0 && !c.isNull(it) }?.let(c::getLong)
            }
        }
    }
    return PickedFile(uri.toString(), name, runCatching { context.contentResolver.getType(uri) }.getOrNull(), size)
}

@Composable private fun CategoryPicker(selected: DocumentCategory, enabled: Boolean, onSelect: (DocumentCategory) -> Unit) {
    Column(Modifier.selectableGroup()) {
        DocumentCategory.entries.forEach { category ->
            Row(Modifier.fillMaxWidth().heightIn(min = JanSetuSpacing.touchTarget)
                .selectable(selected = category == selected, enabled = enabled, role = Role.RadioButton) { onSelect(category) }
                .testTag("document-category:${category.name}"), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = category == selected, onClick = null, enabled = enabled)
                Spacer(Modifier.width(8.dp))
                Text(category.label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Name and category, used both before saving a new file and when editing a saved one. */
@Composable private fun DocumentDetailsDialog(title: String, confirmLabel: String, initialName: String, initialCategory: DocumentCategory,
    busy: Boolean, error: String?, onConfirm: (String, DocumentCategory) -> Unit, onDismiss: () -> Unit, explanation: String? = null) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var category by rememberSaveable { mutableStateOf(initialCategory) }
    val nameErrors = VaultRules.nameErrors(name)
    AlertDialog(onDismissRequest = { if (!busy) onDismiss() }, title = { Text(title) },
        text = {
            Column(Modifier.verticalScrollFallback(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (explanation != null) Text(explanation, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true, enabled = !busy,
                    modifier = Modifier.fillMaxWidth().testTag("document-name"), isError = nameErrors.isNotEmpty(),
                    supportingText = { Text(nameErrors.firstOrNull() ?: "Shown only to you. Up to ${VaultLimits.NAME_MAX} characters.") })
                Text("Category", style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
                CategoryPicker(category, !busy) { category = it }
                if (busy) JanSetuLoading("Encrypting and checking the file…")
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("document-error").semantics { liveRegion = LiveRegionMode.Polite })
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(name.trim(), category) }, enabled = !busy && nameErrors.isEmpty(),
            modifier = Modifier.testTag("document-confirm")) { Text(confirmLabel) } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text("Cancel") } })
}

/** Dialog content can exceed the screen at large text; this keeps every field reachable. */
@Composable private fun Modifier.verticalScrollFallback(): Modifier = this.then(Modifier.verticalScroll(rememberScrollState()))

@OptIn(ExperimentalLayoutApi::class)
@Composable fun MyDocumentsScreen(settings: LocalSettings, vault: VaultDependencies, refresh: Int, onOpen: (String) -> Unit,
    notice: String? = null, onNoticeDone: () -> Unit = {}) {
    SecureWindow()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var snapshot by remember { mutableStateOf<VaultSnapshot?>(null) }
    var failed by remember { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var localNotice by remember { mutableStateOf<String?>(null) }
    var picked by rememberSaveable(stateSaver = androidx.compose.runtime.saveable.listSaver<PickedFile?, String?>(
        save = { listOf(it?.uri, it?.name, it?.mime, it?.size?.toString()) },
        restore = { if (it[0] == null) null else PickedFile(it[0]!!, it[1], it[2], it[3]?.toLongOrNull()) })) { mutableStateOf(null) }
    var importError by remember { mutableStateOf<String?>(null) }
    var confirm by remember { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var categoryName by rememberSaveable { mutableStateOf("") }
    var typeName by rememberSaveable { mutableStateOf("") }
    var sortName by rememberSaveable { mutableStateOf(DocumentSort.NEWEST.name) }
    var pickerOn by remember { mutableStateOf(false) }
    DisposableEffect(notice) { onDispose { if (notice != null) onNoticeDone() } }
    LaunchedEffect(refresh, retry) {
        failed = false
        try {
            snapshot = withContext(Dispatchers.IO) { vault.repository.snapshot() }
            pickerOn = withContext(Dispatchers.IO) { vault.picker.enabled }
        } catch (_: Exception) { failed = true }
    }
    val choose = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) { importError = null; picked = describe(context, uri) }
    }
    fun runAction(success: String, action: suspend () -> Unit) {
        busy = true
        scope.launch {
            try { action(); localNotice = success } catch (_: Exception) { failed = true }
            finally { busy = false; confirm = null; retry++ }
        }
    }
    val all = snapshot?.documents.orEmpty()
    val documents = DocumentQuery.visible(all, query, DocumentCategory.entries.firstOrNull { it.name == categoryName },
        DocumentType.entries.firstOrNull { it.name == typeName }, DocumentSort.valueOf(sortName))
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp).testTag("documents-list"), contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { JanSetuScreenHeader(t(settings, "My Documents", "मेरे दस्तावेज़"),
            subtitle = t(settings, "Encrypted copies of your documents, kept only on this device.", "आपके दस्तावेज़ों की एन्क्रिप्टेड प्रतियाँ, केवल इसी डिवाइस पर।")) }
        item { JanSetuFeedbackNotice(localNotice ?: notice, "Dismiss", { localNotice = null; onNoticeDone() }) }
        item { JanSetuNotice("JanSetu never uploads, sends or submits your documents. When an official website asks for a file, you choose it yourself.", tone = StatusTone.INFO) }
        if (failed) item {
            JanSetuNotice(DOCUMENTS_STORAGE_ERROR, Modifier.semantics { liveRegion = LiveRegionMode.Polite }, tone = StatusTone.ERROR) {
                TextButton(onClick = { retry++ }) { Text("Retry") }
            }
        }
        val current = snapshot
        if (current?.keyState == VaultKeyState.LOCKED) item {
            JanSetuNotice("${current.unreadableIds.size} saved document(s) are locked because this device's security key for My Documents is no longer available " +
                "(for example after security credentials were reset). The encrypted files are kept and have not been deleted, but JanSetu cannot open them, and new documents cannot be added until they are removed.",
                tone = StatusTone.WARNING, title = "Documents locked") {
                TextButton(onClick = { confirm = "locked" }, enabled = !busy, modifier = Modifier.testTag("documents-remove-locked")) { Text("Remove locked documents") }
            }
        }
        item {
            Button(onClick = { choose.launch(DocumentType.mimeTypes) }, enabled = !busy && current != null && current.keyState == VaultKeyState.READY,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("documents-add")) {
                Icon(Icons.Default.Add, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Add a document")
            }
        }
        item { Text("PDF, JPEG or PNG, up to 15 MB each. Use copies you are allowed to keep. Never save passwords or OTPs here.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (all.isNotEmpty()) {
            item {
                OutlinedTextField(query, { query = it }, label = { Text("Search your documents") }, singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Default.Clear, contentDescription = "Clear search") } },
                    modifier = Modifier.fillMaxWidth().testTag("documents-search"))
            }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DocumentMenuChip("Category: " + (DocumentCategory.entries.firstOrNull { it.name == categoryName }?.label ?: "All"), "documents-category",
                        listOf("All categories") + DocumentCategory.entries.map { it.label }, DocumentCategory.entries.indexOfFirst { it.name == categoryName } + 1) {
                        categoryName = if (it == 0) "" else DocumentCategory.entries[it - 1].name }
                    DocumentMenuChip("Type: " + (DocumentType.entries.firstOrNull { it.name == typeName }?.label ?: "All"), "documents-type",
                        listOf("All types") + DocumentType.entries.map { it.label }, DocumentType.entries.indexOfFirst { it.name == typeName } + 1) {
                        typeName = if (it == 0) "" else DocumentType.entries[it - 1].name }
                    DocumentMenuChip("Sort: " + DocumentSort.valueOf(sortName).label, "documents-sort", DocumentSort.entries.map { it.label },
                        DocumentSort.valueOf(sortName).ordinal) { sortName = DocumentSort.entries[it].name }
                }
            }
            item { Text("Showing ${documents.size} of ${all.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
        }
        if (!failed && current == null) item { JanSetuLoading("Loading your documents…") }
        else if (current != null && documents.isEmpty() && current.keyState == VaultKeyState.READY) item {
            JanSetuEmptyState(if (all.isEmpty()) "No documents saved yet" else "No documents match",
                if (all.isEmpty()) "Add a certificate, ID or photo you may need for an application. It stays encrypted on this device." else "Change the search or filters to see more.",
                if (all.isNotEmpty()) "Show all documents" else null,
                if (all.isNotEmpty()) ({ query = ""; categoryName = ""; typeName = "" }) else null, Illustration.CERTIFICATES)
        }
        items(documents, key = { it.id }) { document -> DocumentCard(document) { onOpen(document.id) } }
        if (current != null && current.keyState == VaultKeyState.READY && current.unreadableIds.isNotEmpty()) item {
            JanSetuNotice("${current.unreadableIds.size} saved document(s) cannot be read. They have been kept. Remove them only if you no longer need them.",
                tone = StatusTone.WARNING) {
                TextButton(onClick = { confirm = "unreadable" }, enabled = !busy) { Text("Remove unreadable documents") }
            }
        }
        if (current != null && current.unlistedFiles > 0) item {
            JanSetuNotice("${current.unlistedFiles} encrypted file(s) are not linked to any document, for example after an interrupted save. They cannot be shown.",
                tone = StatusTone.NEUTRAL) {
                TextButton(onClick = { runAction("Unlinked encrypted files removed.") { withContext(Dispatchers.IO) { vault.repository.removeUnlisted() } } }, enabled = !busy) {
                    Text("Remove unlinked files") }
            }
        }
        if (current != null) item {
            Text("Using ${DocumentQuery.sizeLabel(current.usedBytes)} of ${DocumentQuery.sizeLabel(VaultLimits.MAX_TOTAL_BYTES)} · ${current.total} of ${VaultLimits.MAX_DOCUMENTS} documents",
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { PickerAccessCard(pickerOn, !busy && current != null,
            onChange = { value -> runAction(if (value) "My Documents now appears in Android's file picker." else "File-picker access is off. Earlier access was withdrawn.") {
                withContext(Dispatchers.IO) { vault.picker.setEnabled(value) }; pickerOn = value } },
            onRevoke = { runAction("Access already given to apps and websites through the file picker was withdrawn.") { withContext(Dispatchers.IO) { vault.picker.revokeAll() } } }) }
        item { Text(DOCUMENTS_PRIVACY, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (current != null && current.total > 0) item {
            TextButton(onClick = { confirm = "all" }, enabled = !busy, modifier = Modifier.testTag("documents-clear"),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Remove all documents")
            }
        }
    }
    picked?.let { file ->
        DocumentDetailsDialog("Save this document", "Save encrypted", VaultRules.cleanName(file.name), DocumentCategory.OTHER, busy, importError,
            explanation = "JanSetu will keep an encrypted copy. The original file is not changed or deleted.",
            onDismiss = { picked = null; importError = null },
            onConfirm = { name, category ->
                busy = true; importError = null
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            vault.repository.import(VaultImportSource(file.name, file.mime, file.size) {
                                context.contentResolver.openInputStream(file.uri.toUri()) ?: throw IOException("Unavailable")
                            }, category, name)
                        }
                        picked = null; localNotice = "Saved and encrypted on this device."; retry++
                    } catch (error: VaultImportException) { importError = error.problem.message }
                    catch (_: Exception) { importError = ImportProblem.STORAGE.message }
                    finally { busy = false }
                }
            })
    }
    when (confirm) {
        "all" -> JanSetuConfirmDialog("Remove all documents?",
            "Permanently remove every saved document, your preparation choices and the encryption key for My Documents. Copies you exported or gave to other apps are not affected. This cannot be undone.",
            "Remove all", "Cancel", busy = busy, onDismiss = { confirm = null },
            onConfirm = { runAction("All documents were removed from this device.") { withContext(Dispatchers.IO) { vault.repository.clear(); vault.picker.revokeAll() } } })
        "locked", "unreadable" -> JanSetuConfirmDialog(if (confirm == "locked") "Remove locked documents?" else "Remove unreadable documents?",
            "Their contents cannot be read now. Permanently remove only these documents? Readable documents are kept.",
            "Remove", "Cancel", busy = busy, onDismiss = { confirm = null },
            onConfirm = { val ids = snapshot?.unreadableIds.orEmpty()
                runAction("Unreadable documents removed.") { withContext(Dispatchers.IO) { ids.forEach(vault.repository::delete) } } })
    }
}

@Composable private fun PickerAccessCard(enabled: Boolean, available: Boolean, onChange: (Boolean) -> Unit, onRevoke: () -> Unit) {
    JanSetuSectionCard("Use a document on an official website", icon = Icons.Default.FileUpload) {
        Text("When a website asks you to upload a file, your browser opens Android's file picker. If you turn this on, \"JanSetu: My Documents\" appears there, and you can choose one document yourself. The website then receives that one file.",
            style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.fillMaxWidth().heightIn(min = JanSetuSpacing.touchTarget)
            .toggleable(value = enabled, enabled = available, role = Role.Switch, onValueChange = onChange).testTag("documents-picker"),
            verticalAlignment = Alignment.CenterVertically) {
            Text("Show My Documents in Android's file picker", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = null, enabled = available)
        }
        Text("Off by default. An app you choose a document for can read it for up to 1 hour. Turning this off, tapping Stop access or deleting the document ends that at once. A copy already uploaded to a website stays with that website.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        TextButton(onClick = onRevoke, enabled = available && enabled, modifier = Modifier.testTag("documents-revoke")) { Text("Stop all file-picker access now") }
        Text("Without the file picker, open a document and use Export a copy, then choose that copy on the website. An exported copy is not encrypted.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun DocumentCard(document: VaultDocument, onOpen: () -> Unit) {
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth().testTag("document:${document.id}"), shape = JanSetuLayout.cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(typeIcon(document.type), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text(document.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                JanSetuBadge(document.category.label, null)
                JanSetuBadge(document.type.label, null)
                JanSetuStatusLabel("Encrypted on this device", StatusTone.SUCCESS, Icons.Default.Lock)
            }
            Text("Added ${formatInstant(document.importedAt)} · ${DocumentQuery.sizeLabel(document.sizeBytes)}",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** ACTION_CREATE_DOCUMENT with the document's own type and name. */
internal class CreateTypedDocument : ActivityResultContract<Pair<String, String>, Uri?>() {
    override fun createIntent(context: Context, input: Pair<String, String>) = Intent(Intent.ACTION_CREATE_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE).setType(input.first).putExtra(Intent.EXTRA_TITLE, input.second)
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = if (resultCode == Activity.RESULT_OK) intent?.data else null
}

private sealed interface Preview {
    data object Loading : Preview
    data class Picture(val bitmap: Bitmap) : Preview
    data class Pdf(val renderer: PdfRenderer) : Preview
    data class Unavailable(val reason: String) : Preview
}

@Composable fun DocumentDetailScreen(id: String, vault: VaultDependencies, refresh: Int, onBack: () -> Unit, onDeleted: () -> Unit,
    notice: String? = null, onNoticeDone: () -> Unit = {}) {
    SecureWindow()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var document by remember(id) { mutableStateOf<VaultDocument?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf(false) }
    var editError by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    var confirmExport by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<Pair<String, StatusTone>?>(null) }
    DisposableEffect(notice) { onDispose { if (notice != null) onNoticeDone() } }
    LaunchedEffect(id, refresh, reload) {
        try { document = withContext(Dispatchers.IO) { vault.repository.get(id) }; failed = false } catch (_: Exception) { failed = true }
        loaded = true
    }
    val export = rememberLauncherForActivityResult(CreateTypedDocument()) { uri ->
        val d = document
        if (uri != null && d != null) {
            busy = true
            scope.launch {
                message = try {
                    withContext(Dispatchers.IO) {
                        vault.repository.open(d.id).use { reader ->
                            (context.contentResolver.openOutputStream(uri, "wt") ?: throw IOException("Unavailable")).use { reader.copyTo(it) }
                        }
                    }
                    "A copy was saved where you chose. That copy is not encrypted and is outside JanSetu's control." to StatusTone.SUCCESS
                } catch (_: Exception) {
                    runCatching { DocumentsContract.deleteDocument(context.contentResolver, uri) }
                    "The copy could not be saved. Your document in JanSetu is unchanged." to StatusTone.ERROR
                } finally { busy = false }
            }
        }
    }
    JanSetuPage(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        JanSetuScreenHeader("Document", onBack = onBack)
        if (failed) { JanSetuNotice(DOCUMENTS_STORAGE_ERROR, tone = StatusTone.ERROR) { TextButton(onClick = { reload++ }) { Text("Retry") } }; return@JanSetuPage }
        val d = document
        if (d == null) {
            if (loaded) JanSetuEmptyState("This document is no longer available.", "It may have been deleted, or it cannot be read. Other documents are unaffected.", illustration = Illustration.NO_RESULTS)
            else JanSetuLoading("Loading…")
            return@JanSetuPage
        }
        JanSetuFeedbackNotice(notice, "Dismiss", onNoticeDone)
        message?.let { (text, tone) -> JanSetuNotice(text, Modifier.testTag("document-message").semantics { liveRegion = LiveRegionMode.Polite }, tone = tone) }
        Text(d.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
        JanSetuStatusLabel("Encrypted on this device", StatusTone.SUCCESS, Icons.Default.Lock)
        JanSetuSectionCard("Details", icon = Icons.Default.Description) {
            JanSetuFactRow("Category (chosen by you)", d.category.label)
            JanSetuFactRow("File type", d.type.label)
            JanSetuFactRow("Size", DocumentQuery.sizeLabel(d.sizeBytes))
            JanSetuFactRow("Added", formatInstant(d.importedAt))
            if (d.updatedAt != d.importedAt) JanSetuFactRow("Details last changed", formatInstant(d.updatedAt))
        }
        DocumentPreview(d, vault)
        OutlinedButton(onClick = { editError = null; editing = true }, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("document-edit")) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Rename or change category")
        }
        OutlinedButton(onClick = { confirmExport = true }, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("document-export")) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Export a copy")
        }
        Text("Export saves an unencrypted copy wherever you choose, for example to upload it on an official website. JanSetu does not upload or submit it.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedButton(onClick = { confirmDelete = true }, enabled = !busy, modifier = Modifier.fillMaxWidth().testTag("document-delete"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Delete document")
        }
        Text(DOCUMENTS_PRIVACY, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    val d = document
    if (editing && d != null) DocumentDetailsDialog("Edit document details", "Save changes", d.name, d.category, busy, editError,
        onDismiss = { editing = false },
        onConfirm = { name, category ->
            busy = true
            scope.launch {
                try {
                    document = withContext(Dispatchers.IO) { vault.repository.update(d.id, name, category, d.revision) }
                    editing = false; message = "Changes saved." to StatusTone.SUCCESS
                } catch (_: VaultConflictException) { editError = "This document changed. Go back and reopen it before editing." }
                catch (_: Exception) { editError = DOCUMENTS_STORAGE_ERROR }
                finally { busy = false }
            }
        })
    if (confirmExport && d != null) JanSetuConfirmDialog("Export an unencrypted copy?",
        "The copy will not be encrypted. Anyone or any app that can open the place you save it can read it. Delete the copy when you no longer need it.",
        "Choose where to save", "Cancel", destructive = false, onDismiss = { confirmExport = false },
        onConfirm = { confirmExport = false; message = null; export.launch(d.type.mime to d.fileName) })
    if (confirmDelete && d != null) JanSetuConfirmDialog("Delete this document?",
        "Permanently delete \"${d.name}\" from My Documents, and your preparation choices that use it. Copies you exported or uploaded elsewhere are not affected. This cannot be undone.",
        "Delete permanently", "Cancel", busy = busy, onDismiss = { confirmDelete = false },
        onConfirm = {
            busy = true
            scope.launch {
                try { withContext(Dispatchers.IO) { vault.repository.delete(d.id) }; confirmDelete = false; onDeleted() }
                catch (_: Exception) { confirmDelete = false; failed = true }
                finally { busy = false }
            }
        })
}

/** Decrypts in memory only. Images are decoded at a bounded size; PDFs render one page at a time. */
@Composable private fun DocumentPreview(document: VaultDocument, vault: VaultDependencies) {
    val widthPx = LocalWindowInfo.current.containerSize.width
    var preview by remember(document.id) { mutableStateOf<Preview>(Preview.Loading) }
    var page by rememberSaveable(document.id) { mutableIntStateOf(0) }
    var zoom by rememberSaveable(document.id) { mutableFloatStateOf(1f) }
    var pageBitmap by remember(document.id) { mutableStateOf<Bitmap?>(null) }
    var show by rememberSaveable(document.id) { mutableStateOf(false) }
    JanSetuSectionCard("Preview", icon = typeIcon(document.type)) {
        if (!show) {
            Text("The preview is decrypted in memory only and is hidden from screenshots.", style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { show = true }, modifier = Modifier.testTag("document-show")) { Text("Show preview") }
            return@JanSetuSectionCard
        }
        LaunchedEffect(document.id) {
            preview = withContext(Dispatchers.IO) {
                try {
                    if (!document.previewAvailable) Preview.Unavailable("This PDF is password-protected, so JanSetu cannot show it. It is stored safely and you can still export or choose it.")
                    else if (document.type.isImage) {
                        val bytes = vault.repository.open(document.id).use { it.readAll() }
                        try {
                            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                            var sample = 1
                            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 2048) sample *= 2
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
                                ?.let { Preview.Picture(it) } ?: Preview.Unavailable("This image could not be shown.")
                        } finally { bytes.fill(0) }
                    } else Preview.Pdf(PdfRenderer(vault.proxy.open(vault.repository.open(document.id))))
                } catch (_: Exception) { Preview.Unavailable("The preview could not be shown. The document is unchanged.") }
            }
        }
        DisposableEffect(document.id) {
            onDispose {
                // Bitmaps are left to the garbage collector: recycling one Compose may still draw would crash.
                (preview as? Preview.Pdf)?.let { runCatching { it.renderer.close() } }
            }
        }
        when (val p = preview) {
            Preview.Loading -> JanSetuLoading("Decrypting preview…")
            is Preview.Unavailable -> Text(p.reason, style = MaterialTheme.typography.bodyMedium)
            is Preview.Picture -> {
                ZoomControls(zoom) { zoom = it }
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val base = maxWidth
                    Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        Image(p.bitmap.asImageBitmap(), contentDescription = "Preview of ${document.name}", contentScale = ContentScale.FillWidth,
                            modifier = Modifier.width(base * zoom).testTag("document-preview"))
                    }
                }
            }
            is Preview.Pdf -> {
                val count = p.renderer.pageCount
                LaunchedEffect(p, page, zoom) {
                    val target = page.coerceIn(0, count - 1)
                    val bitmap = withContext(Dispatchers.IO) {
                        runCatching {
                            synchronized(p.renderer) {
                                p.renderer.openPage(target).use { pdfPage ->
                                    val width = (widthPx * zoom).toInt().coerceIn(200, 4096)
                                    val height = (width.toLong() * pdfPage.height / pdfPage.width).toInt().coerceIn(1, 8192)
                                    createBitmap(width, height).also {
                                        it.eraseColor(android.graphics.Color.WHITE)
                                        pdfPage.render(it, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                    }
                                }
                            }
                        }.getOrNull()
                    }
                    pageBitmap = bitmap
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { page = (page - 1).coerceAtLeast(0) }, enabled = page > 0) {
                        Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous page") }
                    Text("Page ${page + 1} of $count", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f).testTag("document-page"))
                    IconButton(onClick = { page = (page + 1).coerceAtMost(count - 1) }, enabled = page < count - 1) {
                        Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next page") }
                }
                ZoomControls(zoom) { zoom = it }
                pageBitmap?.let { bitmap ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        val base = maxWidth
                        Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).background(MaterialTheme.colorScheme.surface)) {
                            Image(bitmap.asImageBitmap(), contentDescription = "Page ${page + 1} of ${document.name}", contentScale = ContentScale.FillWidth,
                                modifier = Modifier.width(base * zoom).testTag("document-preview"))
                        }
                    }
                } ?: JanSetuLoading("Rendering page…")
            }
        }
    }
}

@Composable private fun ZoomControls(zoom: Float, onZoom: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onZoom((zoom - 0.5f).coerceAtLeast(1f)) }, enabled = zoom > 1f) { Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out") }
        Text("${(zoom * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
        IconButton(onClick = { onZoom((zoom + 0.5f).coerceAtMost(3f)) }, enabled = zoom < 3f) { Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in") }
    }
}

/**
 * Preparation checklist for one service. It lists only the document requirements already recorded from the
 * official source, keeps required, conditional and unconfirmed items apart, and records which saved file the
 * person chose. A chosen file is never described as meeting a requirement, and nothing is uploaded.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable fun PrepareDocumentsScreen(service: Service?, settings: LocalSettings, vault: VaultDependencies, refresh: Int,
    onBack: () -> Unit, onLink: (String) -> Unit, onDocuments: () -> Unit, onTrack: (() -> Unit)? = null) {
    SecureWindow()
    val scope = rememberCoroutineScope()
    var snapshot by remember { mutableStateOf<VaultSnapshot?>(null) }
    var choices by remember { mutableStateOf<List<PreparationChoice>?>(null) }
    var failed by remember { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    var choosingFor by remember { mutableStateOf<RequirementItem?>(null) }
    var busy by remember { mutableStateOf(false) }
    LaunchedEffect(refresh, reload) {
        try {
            val (s, c) = withContext(Dispatchers.IO) { vault.repository.snapshot() to vault.repository.choices() }
            snapshot = s; choices = c; failed = false
        } catch (_: Exception) { failed = true }
    }
    fun act(block: suspend () -> Unit) {
        busy = true
        scope.launch { try { block() } catch (_: Exception) { failed = true } finally { busy = false; choosingFor = null; reload++ } }
    }
    JanSetuPage(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        JanSetuScreenHeader("Prepare documents", onBack = onBack)
        if (service == null) {
            JanSetuEmptyState("Service unavailable", "This service is not in the current catalog.", "Go back", onBack, Illustration.NO_RESULTS); return@JanSetuPage
        }
        Text(service.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
        JanSetuBadge(serviceCoverage(service, settings), null, emphasized = true)
        JanSetuNotice("A private checklist on this device. It shows only the document requirements JanSetu recorded from the official source (checked ${service.verifiedOn}); the list may be partial. Choosing a file records your own choice. JanSetu does not check, upload or submit anything.",
            tone = StatusTone.INFO)
        if (failed) JanSetuNotice(DOCUMENTS_STORAGE_ERROR, tone = StatusTone.ERROR) { TextButton(onClick = { reload++ }) { Text("Retry") } }
        val requirements = Preparation.requirements(service)
        val view = if (snapshot != null && choices != null) Preparation.view(requirements, choices.orEmpty(), snapshot!!.documents) else null
        if (view == null && !failed) { JanSetuLoading("Loading your checklist…"); return@JanSetuPage }
        view ?: return@JanSetuPage
        Text(view.summary(), style = MaterialTheme.typography.titleSmall, modifier = Modifier.testTag("prepare-summary").semantics { liveRegion = LiveRegionMode.Polite })
        @Composable fun group(title: String, items: List<PreparedItem>?, missing: String) {
            JanSetuSectionCard(title, icon = Icons.Default.Description) {
                if (items == null) Text(missing, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                items?.forEachIndexed { index, item ->
                    if (index > 0) HorizontalDivider()
                    RequirementRow(item, busy, onLink, onChoose = { choosingFor = item.requirement },
                        onRemove = { choice -> act { withContext(Dispatchers.IO) { vault.repository.removeChoice(choice.id) } } })
                }
            }
        }
        group(RequirementKind.REQUIRED.label, requirements.required?.let { view.items.filter { it.requirement.kind == RequirementKind.REQUIRED } },
            "Not confirmed in JanSetu's records. Check the official information page for the documents you need.")
        group(RequirementKind.CONDITIONAL.label, requirements.conditional?.let { view.items.filter { it.requirement.kind == RequirementKind.CONDITIONAL } },
            "No conditional documents are confirmed in JanSetu's records. The official page may list some.")
        if (view.outdatedChoices.isNotEmpty()) JanSetuSectionCard("Earlier choices for changed requirements") {
            Text("The service record changed after you chose these files. Your choices are kept so you can review them.", style = MaterialTheme.typography.bodySmall)
            view.outdatedChoices.forEach { (choice, document) ->
                Text("“${choice.requirementText}”: ${document?.name ?: "document no longer saved"}", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { act { withContext(Dispatchers.IO) { vault.repository.removeChoice(choice.id) } } }, enabled = !busy) { Text("Remove this choice") }
            }
        }
        JanSetuOfficialLinkButton("Official service information", "Confirm the current document list with the department before you apply.", { onLink(service.informationUrl) }, primary = false)
        OutlinedButton(onClick = onDocuments, modifier = Modifier.fillMaxWidth().testTag("prepare-documents")) { Text("Open My Documents") }
        if (onTrack != null && service.hasConfirmedApplicationRoute()) {
            OutlinedButton(onClick = onTrack, modifier = Modifier.fillMaxWidth().testTag("prepare-track")) { Text("Add to My Applications") }
            Text("Create a local record of your own progress for this service. This does not submit an application.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("To use a chosen file on the official website, turn on file-picker access in My Documents, or export a copy, and select it yourself when the site asks.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    choosingFor?.let { item ->
        val documents = snapshot?.documents.orEmpty()
        ChooseDocumentDialog(item, documents, busy, onDismiss = { choosingFor = null }, onDocuments = { choosingFor = null; onDocuments() },
            onChoose = { document -> service?.let { s -> act { withContext(Dispatchers.IO) { vault.repository.choose(Preparation.newChoice(s.id, item, document.id, Instant.now())) } } } })
    }
}

@Composable private fun RequirementRow(item: PreparedItem, busy: Boolean, onLink: (String) -> Unit, onChoose: () -> Unit, onRemove: (PreparationChoice) -> Unit) {
    Column(Modifier.fillMaxWidth().testTag("requirement:${item.requirement.key}"), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(item.requirement.text, style = MaterialTheme.typography.bodyLarge)
        item.requirement.sources.forEach { source ->
            TextButton(onClick = { onLink(source.url) }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                Text("Source: ${source.publisher} · checked ${source.checkedOn}", style = MaterialTheme.typography.labelMedium)
            }
        }
        if (item.chosen.isEmpty()) JanSetuStatusLabel("No file chosen", StatusTone.NEUTRAL, null)
        item.chosen.forEach { (choice, document) ->
            Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh, JanSetuLayout.chipShape).padding(10.dp)) {
                Text("You chose: ${document?.name ?: "a document that is no longer saved"}", style = MaterialTheme.typography.titleSmall)
                Text(NOT_CHECKED, style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { onRemove(choice) }, enabled = !busy) { Text("Remove choice") }
            }
        }
        OutlinedButton(onClick = onChoose, enabled = !busy, modifier = Modifier.testTag("requirement-choose:${item.requirement.key}")) { Text("Choose a saved document") }
    }
}

@Composable private fun ChooseDocumentDialog(item: RequirementItem, documents: List<VaultDocument>, busy: Boolean, onDismiss: () -> Unit,
    onDocuments: () -> Unit, onChoose: (VaultDocument) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val visible = DocumentQuery.visible(documents, query, null, null, DocumentSort.NAME)
    AlertDialog(onDismissRequest = { if (!busy) onDismiss() }, title = { Text("Choose a saved document") },
        text = {
            Column(Modifier.verticalScrollFallback(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("For: ${item.text}", style = MaterialTheme.typography.bodyMedium)
                Text("This records your choice only. $NOT_CHECKED", style = MaterialTheme.typography.bodySmall)
                if (documents.isEmpty()) Text("You have not saved any documents yet.", style = MaterialTheme.typography.bodyMedium)
                else OutlinedTextField(query, { query = it }, label = { Text("Search") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                visible.forEach { document ->
                    Row(Modifier.fillMaxWidth().heightIn(min = JanSetuSpacing.touchTarget)
                        .selectable(selected = false, enabled = !busy, role = Role.Button) { onChoose(document) }.testTag("choose:${document.id}"),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(typeIcon(document.type), contentDescription = null); Spacer(Modifier.width(8.dp))
                        Column { Text(document.name, style = MaterialTheme.typography.titleSmall); Text(document.category.label, style = MaterialTheme.typography.bodySmall) }
                    }
                }
                TextButton(onClick = onDocuments, enabled = !busy) { Text("Add a document in My Documents") }
            }
        },
        confirmButton = {}, dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text("Cancel") } })
}
