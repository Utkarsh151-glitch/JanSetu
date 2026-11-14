package `in`.jansetu.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable

/** Shared visual primitives for every citizen-facing Compose screen. */
object JanSetuLayout {
    val pageHorizontal = 20.dp
    val pageVertical = 20.dp
    val sectionGap = 18.dp
    val itemGap = 12.dp
    val cardPadding = 18.dp
    val maxContentWidth = 720.dp
    val cardShape = RoundedCornerShape(18.dp)
    val chipShape = RoundedCornerShape(10.dp)
}

@Composable
fun JanSetuPage(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(JanSetuLayout.sectionGap),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .widthIn(max = JanSetuLayout.maxContentWidth)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = JanSetuLayout.pageHorizontal, vertical = JanSetuLayout.pageVertical),
            verticalArrangement = verticalArrangement,
            content = content
        )
    }
}

@Composable
fun JanSetuScreenHeader(
    title: String,
    subtitle: String? = null,
    backLabel: String = "Back",
    onBack: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backLabel)
                }
                Spacer(Modifier.width(4.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            action?.invoke()
        }
    }
}

@Composable
fun JanSetuSectionTitle(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f).semantics { heading() })
        if (action != null && onAction != null) TextButton(onClick = onAction) { Text(action) }
    }
}

@Composable
fun JanSetuInfoCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    val colors = CardDefaults.cardColors(
        containerColor = if (emphasized) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
    )
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = JanSetuLayout.cardShape,
        colors = colors,
        border = if (emphasized) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(JanSetuLayout.cardPadding), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() })
            if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content?.invoke(this)
        }
    }
}

/** Meaning is always given by words and an icon, never by colour alone. */
enum class StatusTone { INFO, SUCCESS, WARNING, ERROR, NEUTRAL }

private data class ToneColors(val container: Color, val content: Color, val icon: ImageVector)

@Composable
private fun toneColors(tone: StatusTone): ToneColors {
    val scheme = MaterialTheme.colorScheme
    val extra = JanSetuTheme.colors
    return when (tone) {
        StatusTone.INFO -> ToneColors(scheme.secondaryContainer, scheme.onSecondaryContainer, Icons.Default.Info)
        StatusTone.SUCCESS -> ToneColors(extra.successContainer, extra.onSuccessContainer, Icons.Default.CheckCircle)
        StatusTone.WARNING -> ToneColors(extra.warningContainer, extra.onWarningContainer, Icons.Default.WarningAmber)
        StatusTone.ERROR -> ToneColors(scheme.errorContainer, scheme.onErrorContainer, Icons.Default.ErrorOutline)
        StatusTone.NEUTRAL -> ToneColors(scheme.surfaceContainerHigh, scheme.onSurface, Icons.Default.Info)
    }
}

@Composable
fun JanSetuNotice(message: String, modifier: Modifier = Modifier, isError: Boolean = false) =
    JanSetuNotice(message, modifier, if (isError) StatusTone.ERROR else StatusTone.INFO)

@Composable
fun JanSetuNotice(message: String, modifier: Modifier = Modifier, tone: StatusTone, title: String? = null,
                  content: (@Composable ColumnScope.() -> Unit)? = null) {
    val colors = toneColors(tone)
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = colors.container) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(colors.icon, contentDescription = null, tint = colors.content, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (title != null) Text(title, color = colors.content, style = MaterialTheme.typography.titleSmall)
                Text(message, color = colors.content, style = MaterialTheme.typography.bodyMedium)
                content?.invoke(this)
            }
        }
    }
}

/** Compact label such as a record type, application mode or tracker status. */
@Composable
fun JanSetuStatusLabel(text: String, tone: StatusTone, icon: ImageVector?, modifier: Modifier = Modifier) {
    val colors = toneColors(tone)
    Surface(shape = JanSetuLayout.chipShape, color = colors.container, modifier = modifier) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon ?: colors.icon, contentDescription = null, tint = colors.content, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, color = colors.content, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Neutral outlined tag, used for jurisdiction and record type. */
@Composable
fun JanSetuBadge(text: String, icon: ImageVector?, modifier: Modifier = Modifier, emphasized: Boolean = false) {
    val color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(shape = JanSetuLayout.chipShape, color = Color.Transparent,
        border = BorderStroke(1.dp, if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline), modifier = modifier) {
        Row(Modifier.padding(horizontal = 9.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
            }
            Text(text, color = color, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/**
 * The single visual for leaving JanSetu for an official site. The caption
 * always names the destination so the brand stays separate from the site.
 */
@Composable
fun JanSetuOfficialLinkButton(label: String, caption: String?, onClick: () -> Unit, modifier: Modifier = Modifier,
                              primary: Boolean = true, enabled: Boolean = true, buttonTag: String? = null) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val content: @Composable () -> Unit = {
            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label)
        }
        val tag = if (buttonTag != null) Modifier.testTag(buttonTag) else Modifier
        if (primary) Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).then(tag)) { content() }
        else OutlinedButton(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).then(tag)) { content() }
        if (caption != null) Text(caption, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Publisher, check date and a link to the original evidence. */
@Composable
fun JanSetuSourceAttribution(publisher: String, checkedOn: String?, detail: String?, linkLabel: String, onOpen: () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(publisher, style = MaterialTheme.typography.titleSmall)
        if (checkedOn != null) Text(checkedOn, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (!detail.isNullOrBlank()) Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        TextButton(onClick = onOpen) {
            Text(linkLabel)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun JanSetuFactRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().semantics(mergeDescendants = true) {}, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Titled card with heading semantics for TalkBack navigation. */
@Composable
fun JanSetuSectionCard(title: String, modifier: Modifier = Modifier, icon: ImageVector? = null,
                       content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth(), shape = JanSetuLayout.cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.padding(JanSetuLayout.cardPadding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                }
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
            }
            content()
        }
    }
}

/**
 * Long, secondary detail such as source evidence. The header stays visible
 * and states how many items are inside, so nothing is hidden without notice.
 */
@Composable
fun JanSetuExpandableSection(title: String, summary: String, expandLabel: String, collapseLabel: String,
                             modifier: Modifier = Modifier, initiallyExpanded: Boolean = false, testTag: String? = null,
                             content: @Composable ColumnScope.() -> Unit) {
    var expanded by rememberSaveable(title) { mutableStateOf(initiallyExpanded) }
    val motion = JanSetuTheme.motion
    Card(modifier.fillMaxWidth().animateContentSize(motion.standard()), shape = JanSetuLayout.cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(Modifier.fillMaxWidth()
            .heightIn(min = JanSetuSpacing.touchTarget)
            .clickable(role = Role.Button, onClickLabel = if (expanded) collapseLabel else expandLabel) { expanded = !expanded }
            .semantics(mergeDescendants = true) { stateDescription = if (expanded) collapseLabel else expandLabel }
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(JanSetuLayout.cardPadding), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                Text(summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
        }
        AnimatedVisibility(expanded, enter = if (motion.reduced) fadeIn(motion.standard()) else expandVertically() + fadeIn(),
            exit = if (motion.reduced) fadeOut(motion.standard()) else shrinkVertically() + fadeOut()) {
            Column(Modifier.padding(start = JanSetuLayout.cardPadding, end = JanSetuLayout.cardPadding, bottom = JanSetuLayout.cardPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
        }
    }
}

/** Explicit confirmation for destructive or replacing actions. */
@Composable
fun JanSetuConfirmDialog(title: String, message: String, confirmLabel: String, dismissLabel: String,
                         onConfirm: () -> Unit, onDismiss: () -> Unit, destructive: Boolean = true, busy: Boolean = false) {
    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        icon = { Icon(if (destructive) Icons.Default.WarningAmber else Icons.Default.Info, contentDescription = null) },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !busy,
                colors = if (destructive) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error) else ButtonDefaults.textButtonColors()) {
                Text(confirmLabel)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text(dismissLabel) } }
    )
}

@Composable
fun JanSetuLoading(message: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(vertical = 12.dp).semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 3.dp)
        Spacer(Modifier.width(12.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun JanSetuEmptyState(title: String, message: String, action: String? = null, onAction: (() -> Unit)? = null,
                      illustration: Illustration? = null) {
    Card(Modifier.fillMaxWidth(), shape = JanSetuLayout.cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Row(Modifier.padding(JanSetuLayout.cardPadding), verticalAlignment = Alignment.Top) {
            if (illustration != null) {
                JanSetuIllustration(illustration, Modifier.size(64.dp))
                Spacer(Modifier.width(14.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (action != null && onAction != null) TextButton(onClick = onAction) { Text(action) }
            }
        }
    }
}

/**
 * Inline confirmation after a save, update or delete. It fades in (instantly
 * with reduced motion), is announced politely by TalkBack, never blocks a
 * control, and the person can dismiss it.
 */
@Composable
fun JanSetuFeedbackNotice(message: String?, dismissLabel: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val motion = JanSetuTheme.motion
    AnimatedVisibility(message != null, enter = fadeIn(motion.standard()), exit = fadeOut(motion.standard())) {
        JanSetuNotice(message.orEmpty(), modifier.testTag("feedback-notice").semantics { liveRegion = LiveRegionMode.Polite }, tone = StatusTone.SUCCESS) {
            TextButton(onClick = onDismiss) { Text(dismissLabel) }
        }
    }
}

@Composable
fun JanSetuPrimaryActionLabel(label: String) {
    Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 2.dp))
}
