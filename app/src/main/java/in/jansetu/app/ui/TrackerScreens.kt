package `in`.jansetu.app.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import `in`.jansetu.app.catalog.Catalog
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.tracker.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val STORAGE_ERROR = "Application records could not be read or saved. Existing data has not been reset. Retry, or deliberately remove tracker data."
private const val USER_STATUS = "Your recorded status · maintained by you, not verified by a government department."
private const val LOCAL_PRIVACY = "Local profile only. No account or cloud sync. Records are encrypted on this device and excluded from backup and transfer. Uninstalling or clearing app storage loses them. Do not enter passwords, OTPs, Aadhaar numbers or identity documents."

/** Status meaning is carried by an icon and the label, with tone only as reinforcement. */
internal fun statusIcon(status: ApplicationStatus): ImageVector = when (status) {
    ApplicationStatus.PLANNING -> Icons.Default.EditCalendar
    ApplicationStatus.PREPARING -> Icons.Default.FolderOpen
    ApplicationStatus.READY -> Icons.Default.TaskAlt
    ApplicationStatus.SUBMITTED -> Icons.AutoMirrored.Filled.Send
    ApplicationStatus.FOLLOW_UP -> Icons.Default.NotificationImportant
    ApplicationStatus.APPROVED -> Icons.Default.CheckCircle
    ApplicationStatus.REJECTED -> Icons.Default.Cancel
    ApplicationStatus.WITHDRAWN -> Icons.Default.RemoveCircleOutline
}

internal fun statusTone(status: ApplicationStatus): StatusTone = when (status) {
    ApplicationStatus.APPROVED -> StatusTone.SUCCESS
    ApplicationStatus.REJECTED -> StatusTone.ERROR
    ApplicationStatus.FOLLOW_UP -> StatusTone.WARNING
    ApplicationStatus.SUBMITTED -> StatusTone.INFO
    else -> StatusTone.NEUTRAL
}

private fun deadlineTone(urgency: DeadlineUrgency) = when (urgency) {
    DeadlineUrgency.PASSED, DeadlineUrgency.TODAY, DeadlineUrgency.SOON -> StatusTone.WARNING
    else -> StatusTone.NEUTRAL
}

@Composable private fun DeadlineLabel(record: ApplicationRecord) {
    val summary = TrackerPresentation.deadline(record.deadline, record.status.closed)
    if (summary.urgency != DeadlineUrgency.NONE) JanSetuStatusLabel(summary.text, deadlineTone(summary.urgency), Icons.Default.Event)
}

@Composable private fun MenuChip(label: String, tag: String, options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable fun MyApplicationsScreen(settings: LocalSettings, tracker: ApplicationTracker, refresh: Int,
    onAdd: () -> Unit, onOpen: (String) -> Unit, notice: String? = null, onNoticeDone: () -> Unit = {}) {
    val scope=rememberCoroutineScope()
    var snapshot by remember { mutableStateOf<TrackerSnapshot?>(null) }
    var error by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    var confirmClear by remember { mutableStateOf(false) }
    var removeUnreadable by remember {mutableStateOf<String?>(null)}
    var localNotice by remember {mutableStateOf<String?>(null)}
    var confirmExport by remember {mutableStateOf(false)}
    val context=LocalContext.current
    val export=rememberLauncherForActivityResult(CreateTypedDocument()) {uri ->
        if(uri!=null) {busy=true;scope.launch {
            localNotice=try {
                val text=withContext(Dispatchers.IO) {ApplicationExport.export(tracker.repository.read(),java.time.Instant.now())}
                withContext(Dispatchers.IO) {(context.contentResolver.openOutputStream(uri,"wt") ?: throw java.io.IOException()).use {it.write(text.toByteArray())}}
                "Your applications were exported where you chose. That file is not encrypted."
            } catch (_:Exception) {
                runCatching {android.provider.DocumentsContract.deleteDocument(context.contentResolver,uri)}
                "The export could not be saved. Your applications in JanSetu are unchanged."
            } finally {busy=false}
        }}
    }
    DisposableEffect(notice) {onDispose {if(notice!=null) onNoticeDone()}}
    var filter by rememberSaveable { mutableStateOf("") }
    var sortName by rememberSaveable { mutableStateOf(ApplicationSort.UPDATED.name) }
    LaunchedEffect(refresh,retry) {
        error=false
        try { snapshot=withContext(Dispatchers.IO) { tracker.repository.read().also { tracker.reconcile() } } }
        catch (_: Exception) { error=true }
    }
    val all=snapshot?.records.orEmpty()
    val records=ApplicationRules.visible(all,ApplicationStatus.entries.firstOrNull {it.name==filter},ApplicationSort.valueOf(sortName))
    // The header belongs to the same scroll container as records. This keeps each
    // saved row reachable when large text leaves little vertical room.
    LazyColumn(Modifier.fillMaxSize().padding(horizontal=20.dp).testTag("tracker-list"),contentPadding=PaddingValues(vertical=20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { JanSetuScreenHeader(t(settings,"My Applications","मेरे आवेदन"),
            subtitle=t(settings,"Your private record of applications made on official websites.","आधिकारिक वेबसाइटों पर किए गए आवेदनों का आपका निजी रिकॉर्ड।")) }
        item { JanSetuFeedbackNotice(localNotice ?: notice,"Dismiss",{localNotice=null;onNoticeDone()}) }
        item { JanSetuNotice(USER_STATUS) }
        item { Button(onClick=onAdd,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp).testTag("tracker-add-manual"),enabled=!busy) {
            Icon(Icons.Default.Add,contentDescription=null);Spacer(Modifier.width(8.dp));Text("Add an application manually") } }
        if(all.isNotEmpty()) item {
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                MenuChip("Status: "+(ApplicationStatus.entries.firstOrNull { it.name==filter }?.label ?: "All statuses"),"tracker-filter",
                    listOf("All statuses")+ApplicationStatus.entries.map {it.label},ApplicationStatus.entries.indexOfFirst {it.name==filter}+1) {
                    filter=if(it==0) "" else ApplicationStatus.entries[it-1].name }
                MenuChip("Sort: "+ApplicationSort.valueOf(sortName).label,"tracker-sort",ApplicationSort.entries.map {it.label},
                    ApplicationSort.valueOf(sortName).ordinal) {sortName=ApplicationSort.entries[it].name}
            }
        }
        if(all.isNotEmpty()) item {
            Text("Showing ${records.size} of ${all.size}",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if(error) {
            item { JanSetuNotice(STORAGE_ERROR,tone=StatusTone.ERROR) { TextButton(onClick={retry++}) {Text("Retry")} } }
        }
        snapshot?.unreadableIds?.takeIf {it.isNotEmpty()}?.let {
            item { JanSetuNotice("${it.size} unreadable record(s) preserved. They have not been reset or overwritten. You can deliberately remove an unreadable record below.",tone=StatusTone.WARNING) }
        }
        if(!error && snapshot==null) item {JanSetuLoading("Loading local applications…")}
        else if(!error && records.isEmpty()) item {
            JanSetuEmptyState(if(all.isEmpty()) "No applications yet" else "No applications match this status",
                "Add one manually, or use Add to My Applications on a service with a confirmed online route. Opening a portal does not record a submission.",
                if(all.isNotEmpty()) "Show all statuses" else null, if(all.isNotEmpty()) ({filter=""}) else null, Illustration.NO_APPLICATIONS)
        }
        items(records,key={it.id}) {record -> ApplicationCard(record) {onOpen(record.id)} }
        items(snapshot?.unreadableIds.orEmpty(),key={"unreadable:$it"}) {id ->
            Card(Modifier.fillMaxWidth(), shape=JanSetuLayout.cardShape,
                colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerLow)) {Column(Modifier.padding(14.dp)) {
                JanSetuStatusLabel("Unreadable application · contents preserved",StatusTone.WARNING,null)
                TextButton(onClick={removeUnreadable=id},enabled=!busy) {Text("Remove this unreadable record")}
            }}
        }
        item {Text(LOCAL_PRIVACY,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}
        if(all.isNotEmpty()) item {OutlinedButton(onClick={confirmExport=true},enabled=!busy,modifier=Modifier.fillMaxWidth().testTag("tracker-export")) {
            Text("Export my applications")}}
        item {TextButton(onClick={confirmClear=true},enabled=!busy,modifier=Modifier.testTag("tracker-clear"),
            colors=ButtonDefaults.textButtonColors(contentColor=MaterialTheme.colorScheme.error)) {
            Icon(Icons.Default.DeleteOutline,contentDescription=null,modifier=Modifier.size(18.dp));Spacer(Modifier.width(6.dp));Text("Remove all tracker data")}}
    }
    if(confirmClear) JanSetuConfirmDialog("Remove all tracker data?",
        "Permanently remove every application and its progress history, and cancel local reminders. Catalogs, saved portals and preferences are preserved. This cannot be undone.",
        "Remove all","Cancel",busy=busy,onDismiss={confirmClear=false},onConfirm={busy=true;scope.launch {
            try {withContext(Dispatchers.IO) {tracker.clear()};snapshot=TrackerSnapshot(emptyList());error=false;confirmClear=false;localNotice="All tracker data was removed from this device."}
            catch (_:Exception) {error=true;confirmClear=false}
            finally {busy=false;retry++}
        }})
    if(confirmExport) JanSetuConfirmDialog("Export an unencrypted copy?",
        "Saves your applications, history, notes and reference numbers as a readable file wherever you choose. It is not encrypted, so anyone or any app that can open that place can read it. The file says that statuses were recorded by you and are not government records.",
        "Choose where to save","Cancel",destructive=false,onDismiss={confirmExport=false},
        onConfirm={confirmExport=false;export.launch("application/json" to ApplicationExport.fileName(java.time.Instant.now()))})
    if(removeUnreadable!=null) JanSetuConfirmDialog("Remove this unreadable record?",
        "Its contents and history cannot currently be read. Remove only this record and its reminder permanently? Other records are preserved.",
        "Remove record","Cancel",busy=busy,onDismiss={removeUnreadable=null},onConfirm={val id=removeUnreadable;if(id!=null) {busy=true;scope.launch {
            try {withContext(Dispatchers.IO) {tracker.delete(id)};removeUnreadable=null;retry++}
            catch (_:Exception) {error=true;removeUnreadable=null}
            finally {busy=false}
        }}})
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun ApplicationCard(record: ApplicationRecord, onOpen: () -> Unit) {
    Card(onClick=onOpen,modifier=Modifier.fillMaxWidth().testTag("application:${record.id}"),
        shape=JanSetuLayout.cardShape, colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerLow),
        border=androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text(record.title,style=MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                JanSetuStatusLabel("Your status: ${record.status.label}",statusTone(record.status),statusIcon(record.status))
                DeadlineLabel(record)
            }
            if(record.department.isNotBlank()) Text(record.department,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Updated ${TrackerPresentation.updatedDate(record.updatedAt)}"+(if(record.service==null) " · Manual entry, no verified catalog link" else " · Linked to ${record.service.serviceName}"),
                style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Returns the validation messages that belong beside one field. */
private fun fieldErrors(errors: List<String>, vararg prefixes: String) = errors.filter { e -> prefixes.any { e.startsWith(it) } }

@Composable private fun FieldError(messages: List<String>) {
    messages.forEach { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.testTag("tracker-error")) }
}

@Composable private fun EditorHeading(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp).semantics { heading() })
}

@Composable private fun CheckRow(checked: Boolean, label: String, tag: String, enabled: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min=JanSetuSpacing.touchTarget)
        .toggleable(value=checked,enabled=enabled,role=Role.Checkbox,onValueChange=onChange).testTag(tag),
        verticalAlignment=Alignment.CenterVertically) {
        Checkbox(checked,onCheckedChange=null,enabled=enabled)
        Spacer(Modifier.width(10.dp))
        Text(label,style=MaterialTheme.typography.bodyMedium)
    }
}

@Composable fun ApplicationEditorScreen(tracker: ApplicationTracker, recordId: String?, seed: ServiceSnapshot?, onBack: () -> Unit, onSaved: (String) -> Unit) {
    val scope=rememberCoroutineScope(); val context=LocalContext.current
    var initialized by rememberSaveable {mutableStateOf(false)}
    var loaded by remember {mutableStateOf(false)}
    var unavailable by remember {mutableStateOf(false)}
    var title by rememberSaveable {mutableStateOf("")}; var department by rememberSaveable {mutableStateOf("")}
    var submitted by rememberSaveable {mutableStateOf("")};var reference by rememberSaveable {mutableStateOf("")}
    var statusName by rememberSaveable {mutableStateOf(ApplicationStatus.PLANNING.name)}
    var deadline by rememberSaveable {mutableStateOf("")};var reminder by rememberSaveable {mutableStateOf(false)}
    var before by rememberSaveable {mutableIntStateOf(0)};var notes by rememberSaveable {mutableStateOf("")}
    var progress by rememberSaveable {mutableStateOf("")};var progressDate by rememberSaveable {mutableStateOf("")}
    var revision by rememberSaveable {mutableLongStateOf(0)};var consent by rememberSaveable {mutableStateOf(false)}
    var reveal by remember {mutableStateOf(false)};var busy by remember {mutableStateOf(false)}
    var errors by remember {mutableStateOf(emptyList<String>())}
    var original by remember {mutableStateOf<ApplicationRecord?>(null)}
    var notificationsEnabled by remember {mutableStateOf(false)}
    val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {granted -> notificationsEnabled=granted}
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle,tracker) {
        val observer=LifecycleEventObserver {_,event -> if(event==Lifecycle.Event.ON_RESUME) notificationsEnabled=tracker.notificationsAvailable()}
        lifecycle.addObserver(observer)
        onDispose {lifecycle.removeObserver(observer)}
    }
    LaunchedEffect(recordId) {
        try {
            original=withContext(Dispatchers.IO) {recordId?.let(tracker.repository::get)}
            if(recordId!=null && original==null) {unavailable=true;return@LaunchedEffect}
            if(!initialized) {
                val r=original
                title=r?.title ?: seed?.serviceName.orEmpty();department=r?.department ?: seed?.department.orEmpty()
                submitted=r?.submittedOn.orEmpty();reference=r?.referenceNumber.orEmpty();statusName=(r?.status ?: ApplicationStatus.PLANNING).name
                deadline=r?.deadline.orEmpty();reminder=r?.reminderEnabled ?: false;before=r?.reminderDaysBefore ?: 0;notes=r?.notes.orEmpty();revision=r?.revision ?: 0
                initialized=true
            }
            notificationsEnabled=tracker.notificationsAvailable();loaded=true
        } catch (_:Exception) {unavailable=true}
    }
    val dateHint="Use YYYY-MM-DD, for example 2026-10-17."
    JanSetuPage(Modifier.imePadding(),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        JanSetuScreenHeader(if(recordId==null) "Add application" else "Edit application",onBack=onBack)
        if(unavailable) {JanSetuNotice(STORAGE_ERROR,tone=StatusTone.ERROR);return@JanSetuPage}
        if(!loaded) {JanSetuLoading("Loading…");return@JanSetuPage}
        JanSetuNotice(USER_STATUS)
        Text(LOCAL_PRIVACY,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        (original?.service ?: seed)?.let {s ->
            JanSetuSectionCard("Linked service",icon=Icons.Default.Link) {
                Text(s.serviceName,style=MaterialTheme.typography.titleSmall)
                Text("${listOfNotNull(s.level,s.state,s.district).joinToString(" · ")}\nOfficial links captured from catalog ${s.catalogVersion}, source checked ${s.checkedOn}.",
                    style=MaterialTheme.typography.bodySmall)
            }
        } ?: Text("Manual entry: no verified service or official link will be inferred.",style=MaterialTheme.typography.bodySmall)

        EditorHeading("About the application")
        val titleErrors=fieldErrors(errors,"Enter a title")
        OutlinedTextField(title,{title=it},label={Text("Application title")},modifier=Modifier.fillMaxWidth().testTag("tracker-title"),singleLine=false,enabled=!busy,
            isError=titleErrors.isNotEmpty(),supportingText=if(titleErrors.isNotEmpty()) ({FieldError(titleErrors)}) else ({Text("Required. Up to 160 characters.")}))
        OutlinedTextField(department,{department=it},label={Text("Department or organization (optional)")},modifier=Modifier.fillMaxWidth(),enabled=!busy,
            isError=department.length>200,supportingText=if(department.length>200) ({Text("Up to 200 characters.",modifier=Modifier.testTag("tracker-error"))}) else null)

        EditorHeading("Status and dates")
        Box {
            var expanded by remember { mutableStateOf(false) }
            val status=ApplicationStatus.valueOf(statusName)
            OutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth().heightIn(min=52.dp).testTag("tracker-status"),enabled=!busy) {
                Icon(statusIcon(status),contentDescription=null,modifier=Modifier.size(18.dp));Spacer(Modifier.width(8.dp))
                Text("Status: ${status.label}",modifier=Modifier.weight(1f));Icon(Icons.Default.ArrowDropDown,contentDescription=null)
            }
            DropdownMenu(expanded,onDismissRequest={expanded=false}) {
                ApplicationStatus.entries.forEach { option -> DropdownMenuItem(text={Text(option.label)},
                    leadingIcon={Icon(statusIcon(option),contentDescription=null)},
                    onClick={expanded=false;statusName=option.name;if(option.closed)reminder=false}) }
            }
        }
        val submittedErrors=fieldErrors(errors,"Submission date")
        OutlinedTextField(submitted,{submitted=it},label={Text("Submission date (optional)")},modifier=Modifier.fillMaxWidth().testTag("tracker-submitted"),singleLine=true,enabled=!busy,
            isError=submittedErrors.isNotEmpty(),supportingText=if(submittedErrors.isNotEmpty()) ({FieldError(submittedErrors)}) else ({Text(dateHint)}))
        val deadlineErrors=fieldErrors(errors,"Deadline")
        OutlinedTextField(deadline,{deadline=it},label={Text("Your deadline (optional)")},modifier=Modifier.fillMaxWidth().testTag("tracker-deadline"),singleLine=true,enabled=!busy,
            isError=deadlineErrors.isNotEmpty(),supportingText=if(deadlineErrors.isNotEmpty()) ({FieldError(deadlineErrors)}) else ({Text(dateHint)}))
        Text("Dates are entered by you; JanSetu does not confirm an official deadline or submission.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        CheckRow(reminder,"Remind me about this deadline","tracker-reminder",!busy) {reminder=it}
        val reminderErrors=fieldErrors(errors,"A valid deadline is required","The reminder time","Turn off reminders","Choose an available reminder")
        FieldError(reminderErrors)
        if(reminder) {
            Box {
                var expanded by remember { mutableStateOf(false) }
                OutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth().testTag("tracker-reminder-days"),enabled=!busy) {
                    Icon(Icons.Default.Notifications,contentDescription=null,modifier=Modifier.size(18.dp));Spacer(Modifier.width(8.dp))
                    Text("Remind: $before day(s) before",modifier=Modifier.weight(1f));Icon(Icons.Default.ArrowDropDown,contentDescription=null)
                }
                DropdownMenu(expanded,onDismissRequest={expanded=false}) {
                    listOf(0,1,3,7).forEach { days -> DropdownMenuItem(text={Text("$days day(s) before")},onClick={expanded=false;before=days}) }
                }
            }
            Text("Around 09:00 in the device's current timezone. Android may delay reminders. No reminder is guaranteed; no internet is required.",
                style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            if(!notificationsEnabled) {
                Text(ReminderState.NOTIFICATIONS_OFF.explanation,style=MaterialTheme.typography.bodyMedium)
                TextButton(onClick={
                    if(Build.VERSION.SDK_INT>=33 && androidx.core.content.ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED)
                        permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else runCatching { context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,context.packageName)) }
                }) {Text("Enable notifications")}
                TextButton(onClick={runCatching {context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,context.packageName))}}) {
                    Text("Notification settings (including after denial)")
                }
            }
        }

        EditorHeading("Reference number")
        OutlinedTextField(reference,{reference=it},label={Text("Reference number (optional)")},modifier=Modifier.fillMaxWidth().testTag("tracker-reference"),
            visualTransformation=if(reveal) VisualTransformation.None else PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(autoCorrectEnabled=false,keyboardType=KeyboardType.Password),enabled=!busy,
            trailingIcon={IconButton(onClick={reveal=!reveal}) {Icon(if(reveal) Icons.Default.VisibilityOff else Icons.Default.Visibility,contentDescription=if(reveal) "Hide reference" else "Show reference")}},
            isError=reference.length>120,supportingText={Text(if(reference.length>120) "Up to 120 characters." else "Hidden by default. Stored encrypted on this device.")})

        EditorHeading("Notes and progress")
        OutlinedTextField(notes,{notes=it},label={Text("Private notes (optional)")},modifier=Modifier.fillMaxWidth().testTag("tracker-notes"),minLines=3,enabled=!busy,
            isError=notes.length>4000,supportingText=if(notes.length>4000) ({Text("Up to 4000 characters.")}) else null)
        OutlinedTextField(progress,{progress=it},label={Text("Progress note for history (optional)")},modifier=Modifier.fillMaxWidth().testTag("tracker-progress-note"),enabled=!busy,
            isError=progress.length>1000,supportingText=if(progress.length>1000) ({Text("Up to 1000 characters.")}) else null)
        val progressErrors=fieldErrors(errors,"Progress date")
        OutlinedTextField(progressDate,{progressDate=it},label={Text("Progress date (optional, defaults to today)")},modifier=Modifier.fillMaxWidth(),singleLine=true,enabled=!busy,
            isError=progressErrors.isNotEmpty(),supportingText=if(progressErrors.isNotEmpty()) ({FieldError(progressErrors)}) else ({Text(dateHint)}))
        Text("Saving a status change appends to history. Earlier progress remains intact.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        if(recordId==null) {
            CheckRow(consent,"Store this application on this device under the local privacy terms above.","tracker-consent",!busy) {consent=it}
            FieldError(fieldErrors(errors,"Confirm local storage"))
        }
        val general=errors.filterNot {e -> listOf("Enter a title","Submission date","Deadline","Progress date","A valid deadline is required","The reminder time","Turn off reminders","Choose an available reminder","Confirm local storage").any {e.startsWith(it)}}
        if(errors.isNotEmpty()) JanSetuNotice(if(errors.size==1) "1 item needs attention before saving." else "${errors.size} items need attention before saving.",
            Modifier.testTag("tracker-error").semantics {liveRegion=LiveRegionMode.Polite},tone=StatusTone.ERROR) {
            general.forEach {Text(it)}
            if(general.size<errors.size) Text("Check the highlighted fields above.")
        }
        Button(enabled=!busy,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp).testTag("tracker-save"),onClick={
            val draft=ApplicationDraft(title,department,submitted.trim(),reference,ApplicationStatus.valueOf(statusName),deadline.trim(),reminder,before,notes,progress,progressDate.trim())
            errors=ApplicationRules.errors(draft)+(if(recordId==null && !consent) listOf("Confirm local storage before saving.") else emptyList())
            if(errors.isEmpty()) {busy=true;scope.launch {
                try { val result=withContext(Dispatchers.IO) {tracker.save(draft,recordId,revision.takeIf {recordId!=null},seed)};onSaved(result.first.id) }
                catch (_:TrackerConflictException) {errors=listOf("This record changed. Go back and reopen it before saving.")}
                catch (_:Exception) {errors=listOf(STORAGE_ERROR)}
                finally {busy=false}
            }}
        }) {Text(if(busy) "Saving…" else "Save application")}
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun ApplicationDetailScreen(id: String, tracker: ApplicationTracker, catalog: Catalog?, refresh: Int,
    onBack: () -> Unit, onEdit: () -> Unit, onDeleted: () -> Unit, onLink: (String) -> Unit,
    notice: String? = null, onNoticeDone: () -> Unit = {},
    vault: `in`.jansetu.app.vault.VaultDependencies? = null, onPrepare: ((String) -> Unit)? = null) {
    val scope=rememberCoroutineScope()
    DisposableEffect(notice) {onDispose {if(notice!=null) onNoticeDone()}}
    var record by remember(id) {mutableStateOf<ApplicationRecord?>(null)}
    var loaded by remember {mutableStateOf(false)};var error by remember {mutableStateOf(false)}
    var reveal by remember {mutableStateOf(false)};var confirmDelete by remember {mutableStateOf(false)};var busy by remember {mutableStateOf(false)}
    var reminder by remember {mutableStateOf(ReminderState.OFF)}
    LaunchedEffect(id,refresh) {
        try { record=withContext(Dispatchers.IO) {tracker.repository.get(id)};record?.let {reminder=withContext(Dispatchers.IO) {tracker.sync(it)}};loaded=true;error=false }
        catch (_:Exception) {error=true;loaded=true}
    }
    JanSetuPage(verticalArrangement=Arrangement.spacedBy(14.dp)) {
        JanSetuScreenHeader("Application details",onBack=onBack)
        if(error) {JanSetuNotice(STORAGE_ERROR,tone=StatusTone.ERROR);return@JanSetuPage}
        val r=record
        if(r==null) {
            if(loaded) JanSetuEmptyState("This application is no longer saved.","It may have been deleted. Other records are unaffected.",illustration=Illustration.NO_APPLICATIONS)
            else JanSetuLoading("Loading…")
            return@JanSetuPage
        }
        JanSetuFeedbackNotice(notice,"Dismiss",onNoticeDone)
        Text(r.title,style=MaterialTheme.typography.headlineSmall,modifier=Modifier.semantics {heading()})
        Card(Modifier.fillMaxWidth(),shape=JanSetuLayout.cardShape,colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Icon(statusIcon(r.status),contentDescription=null,tint=MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.width(10.dp))
                    Text("Your recorded status: ${r.status.label}",style=MaterialTheme.typography.titleMedium,color=MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Text("Maintained by you on this device. This is not a decision or confirmation from a government department.",
                    style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onPrimaryContainer)
                DeadlineLabel(r)
            }
        }
        JanSetuSectionCard("Your details",icon=Icons.Default.Edit) {
            Text("Submission date recorded by you: ${TrackerPresentation.formatDate(r.submittedOn) ?: "Not entered"}",style=MaterialTheme.typography.bodyMedium)
            if(r.referenceNumber.isNotBlank()) {
                Text("Reference: ${if(reveal) r.referenceNumber else "••••••••"}",style=MaterialTheme.typography.bodyMedium)
                TextButton(onClick={reveal=!reveal}) {
                    Icon(if(reveal) Icons.Default.VisibilityOff else Icons.Default.Visibility,contentDescription=null,modifier=Modifier.size(18.dp));Spacer(Modifier.width(6.dp))
                    Text(if(reveal) "Hide reference" else "Show reference")
                }
            } else Text("Reference: Not entered",style=MaterialTheme.typography.bodyMedium)
            Text("Your deadline: ${TrackerPresentation.formatDate(r.deadline) ?: "Not entered"}",style=MaterialTheme.typography.bodyMedium)
            if(r.department.isNotBlank()) Text("Department or organization: ${r.department}",style=MaterialTheme.typography.bodyMedium)
        }
        JanSetuSectionCard("Reminder",icon=Icons.Default.Notifications) {
            // Scheduling state only. Delivery is never claimed from a schedule.
            Text(reminder.explanation,style=MaterialTheme.typography.bodyMedium)
            if(r.reminderEnabled) Text("Reminder preference: ${r.reminderDaysBefore} day(s) before, around 09:00 local time",style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        JanSetuSectionCard("Linked service",icon=Icons.Default.Link) {
            r.service?.let {s ->
                Text("Service captured: ${s.serviceName}\n${listOfNotNull(s.level,s.state,s.district).joinToString(" · ")}",style=MaterialTheme.typography.bodyMedium)
                Text("Catalog ${s.catalogVersion} · source checked ${s.checkedOn}. This is a saved snapshot; confirm current links and rules.",style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
                val currentService=catalog?.services?.firstOrNull {it.id==s.serviceId}
                if(catalog!=null && currentService==null) JanSetuNotice("The linked service is not in the current catalog. Your record and history are preserved.",tone=StatusTone.WARNING)
                JanSetuOfficialLinkButton("Captured official information",null,{onLink(s.informationUrl)},primary=false)
                if(currentService?.hasConfirmedApplicationRoute()==true && currentService.applicationUrl==s.applicationUrl) {
                    JanSetuOfficialLinkButton("Captured official application route",null,{onLink(s.applicationUrl)},primary=false)
                } else if(catalog!=null) JanSetuNotice("The saved application route is no longer confirmed in the current catalog. Your record and history are preserved.",tone=StatusTone.WARNING)
                TextButton(onClick={onLink(s.sourceUrl)}) {Text("Captured source")}
                if(currentService!=null && vault!=null && onPrepare!=null) PreparationSummary(currentService,vault,refresh) {onPrepare(currentService.id)}
            } ?: Text("Manual entry · no verified service or official link",style=MaterialTheme.typography.bodyMedium)
        }
        if(r.notes.isNotBlank()) JanSetuSectionCard("Private notes") {Text(r.notes,style=MaterialTheme.typography.bodyMedium)}
        Button(onClick=onEdit,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp).testTag("tracker-edit")) {
            Icon(Icons.Default.Edit,contentDescription=null,modifier=Modifier.size(18.dp));Spacer(Modifier.width(8.dp));Text("Edit application or record progress")
        }
        Text("Progress history",style=MaterialTheme.typography.titleLarge,modifier=Modifier.semantics {heading()})
        Text("Newest first. Every entry was recorded by you.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        val events=r.history.asReversed()
        Column {
            events.forEachIndexed {index,event -> HistoryEntry(event,last=index==events.lastIndex)}
        }
        Text("Created: ${TrackerPresentation.formatInstant(r.createdAt)}\nUpdated: ${TrackerPresentation.formatInstant(r.updatedAt)}",style=MaterialTheme.typography.bodySmall,
            color=MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedButton(onClick={confirmDelete=true},modifier=Modifier.fillMaxWidth().testTag("tracker-delete"),
            colors=ButtonDefaults.outlinedButtonColors(contentColor=MaterialTheme.colorScheme.error)) {
            Icon(Icons.Default.DeleteOutline,contentDescription=null,modifier=Modifier.size(18.dp));Spacer(Modifier.width(8.dp));Text("Delete application")
        }
    }
    if(confirmDelete) JanSetuConfirmDialog("Delete this application?",
        "Permanently delete this record and its history, and cancel its reminder. Other applications are preserved. This cannot be undone.",
        "Delete permanently","Cancel",busy=busy,onDismiss={confirmDelete=false},onConfirm={busy=true;scope.launch {
            try {withContext(Dispatchers.IO) {tracker.delete(id)};confirmDelete=false;onDeleted()}
            catch (_:Exception) {error=true;confirmDelete=false}
            finally {busy=false}
        }})
}

/** One timeline row: a status dot and connecting line, then the recorded entry. */
@Composable private fun HistoryEntry(event: ProgressEvent, last: Boolean) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).semantics(mergeDescendants=true) {}) {
        Column(Modifier.width(28.dp).fillMaxHeight(),horizontalAlignment=Alignment.CenterHorizontally) {
            Box(Modifier.padding(top=4.dp).size(14.dp).background(MaterialTheme.colorScheme.primary,CircleShape))
            if(!last) Box(Modifier.width(2.dp).weight(1f).background(MaterialTheme.colorScheme.outlineVariant))
        }
        Column(Modifier.weight(1f).padding(start=6.dp,bottom=if(last) 0.dp else 16.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
            Text("${TrackerPresentation.formatDate(event.occurredOn) ?: event.occurredOn} · ${event.status.label}",style=MaterialTheme.typography.titleSmall)
            if(event.note.isNotBlank()) Text(event.note,style=MaterialTheme.typography.bodyMedium)
            Text("Recorded by you · ${TrackerPresentation.formatInstant(event.recordedAt)}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Documents for a linked service: the person's own choices from My Documents, read from the vault's separate
 * encrypted store. It reports choices only and never says a requirement is met.
 */
@Composable private fun PreparationSummary(service: `in`.jansetu.app.catalog.Service, vault: `in`.jansetu.app.vault.VaultDependencies,
    refresh: Int, onPrepare: () -> Unit) {
    val requirements=remember(service) {`in`.jansetu.app.vault.Preparation.requirements(service)}
    if(!requirements.hasAny) return
    var view by remember(service.id) {mutableStateOf<`in`.jansetu.app.vault.PreparationView?>(null)}
    LaunchedEffect(service.id,refresh) {
        view=runCatching {withContext(Dispatchers.IO) {
            `in`.jansetu.app.vault.Preparation.view(requirements,vault.repository.choices(),vault.repository.snapshot().documents)
        }}.getOrNull()
    }
    HorizontalDivider()
    Text("Documents for this service",style=MaterialTheme.typography.titleSmall,modifier=Modifier.semantics {heading()})
    Text(view?.summary() ?: "${requirements.items.size} document item(s) are listed in the service record. Open the checklist to choose your saved files.",
        style=MaterialTheme.typography.bodyMedium,modifier=Modifier.testTag("tracker-preparation"))
    if(view?.outdatedChoices?.isNotEmpty()==true) JanSetuNotice("The document list for this service changed after you chose some files. Review your earlier choices in the checklist.",tone=StatusTone.WARNING)
    OutlinedButton(onClick=onPrepare,modifier=Modifier.fillMaxWidth().testTag("tracker-prepare")) {Text("Prepare documents for this service")}
}
