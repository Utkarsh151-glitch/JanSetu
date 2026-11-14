package `in`.jansetu.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * Original JanSetu artwork, drawn for this project as vector paths (see
 * docs/PHASE5_ASSET_CREDITS.md). The motif is a bridge joining two places:
 * a citizen and a public service. Nothing here reproduces a government
 * emblem, seal, flag or department mark. Colours come from the active theme,
 * so every asset follows light/dark mode and needs no network.
 */

private class ArtPalette(val surface: Color, val line: Color, val accent: Color, val paper: Color, val soft: Color)

@Composable
private fun artPalette(): ArtPalette {
    val scheme = MaterialTheme.colorScheme
    val extra = JanSetuTheme.colors
    return ArtPalette(extra.illustrationSurface, extra.illustrationLine, extra.brandAccent, scheme.surface, scheme.primaryContainer)
}

private fun art(name: String, width: Float, height: Float, block: ImageVector.Builder.() -> Unit): ImageVector =
    ImageVector.Builder(name, width.dp, height.dp, width, height).apply(block).build()

private fun ImageVector.Builder.fill(color: Color, d: String) {
    addPath(addPathNodes(d), fill = SolidColor(color))
}

private fun ImageVector.Builder.stroke(color: Color, width: Float, d: String, fill: Color? = null) {
    addPath(addPathNodes(d), fill = fill?.let(::SolidColor), stroke = SolidColor(color), strokeLineWidth = width,
        strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
}

private fun circle(cx: Float, cy: Float, r: Float) = "M${cx - r},$cy A$r,$r 0 1,1 ${cx + r},$cy A$r,$r 0 1,1 ${cx - r},$cy Z"

/** Same geometry as the launcher foreground and docs/brand/jansetu-mark.svg. */
internal fun logoMarkVector(tile: Color, line: Color, accent: Color): ImageVector = art("JanSetuMark", 48f, 48f) {
    fill(tile, "M12,0 H36 A12,12 0 0 1 48,12 V36 A12,12 0 0 1 36,48 H12 A12,12 0 0 1 0,36 V12 A12,12 0 0 1 12,0 Z")
    stroke(line, 3.6f, "M11,30 Q24,4 37,30")
    stroke(line, 2.2f, "M17.5,21.9 V30 M24,18.8 V30 M30.5,21.9 V30")
    stroke(line, 3.6f, "M7,30 H41")
    fill(accent, circle(7f, 30f, 3.6f))
    fill(accent, circle(41f, 30f, 3.6f))
}

/** JanSetu's icon-only mark. Decorative by default so TalkBack does not repeat the app name. */
@Composable
fun JanSetuLogoMark(modifier: Modifier = Modifier, size: Dp = 40.dp, contentDescription: String? = null) {
    val scheme = MaterialTheme.colorScheme
    val accent = if (JanSetuTheme.isDark) Color(0xFF8A5A08) else Color(0xFFF2C46D)
    val mark = remember(scheme.primary, scheme.onPrimary, accent) { logoMarkVector(scheme.primary, scheme.onPrimary, accent) }
    Image(mark, contentDescription, modifier.size(size).testTag("jansetu-logo"))
}

/**
 * Mark plus name. The name is real text so it scales with the person's font
 * size and reads once as a heading. The mark itself stays decorative.
 */
@Composable
fun JanSetuWordmark(modifier: Modifier = Modifier, tagline: String? = null, markSize: Dp = 36.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        JanSetuLogoMark(size = markSize)
        Column {
            Text("JanSetu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.semantics { heading() })
            if (tagline != null) Text(tagline, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

enum class Illustration { HERO, CERTIFICATES, EDUCATION, EMPLOYMENT, PENSIONS, BUSINESS, GRIEVANCES, NO_RESULTS, NO_APPLICATIONS, NO_SAVED, OFFLINE, HELP }

private fun heroVector(p: ArtPalette) = art("JanSetuHero", 320f, 150f) {
    // Ground and bridge between a citizen (left) and a public-service office (right).
    stroke(p.line, 2.5f, "M6,140 H314")
    stroke(p.line, 6f, "M70,126 Q160,38 250,126")
    stroke(p.line, 3f, "M100,104 V126 M130,90 V126 M160,85 V126 M190,90 V126 M220,104 V126")
    stroke(p.line, 6f, "M56,126 H256")
    fill(p.accent, circle(56f, 126f, 8f))
    fill(p.accent, circle(256f, 126f, 8f))
    // Citizen with a phone.
    fill(p.line, circle(24f, 82f, 10f))
    fill(p.line, "M8,140 V108 A16,16 0 0 1 40,108 V140 Z")
    stroke(p.line, 2f, "M34,100 H46 V118 H34 Z", fill = p.accent)
    // Generic service office; deliberately has no emblem, flag or seal.
    stroke(p.line, 3f, "M268,74 H312 V140 H268 Z", fill = p.paper)
    fill(p.line, "M262,76 L290,54 L318,76 Z")
    fill(p.line, "M283,114 H297 V140 H283 Z")
    fill(p.accent, "M274,86 H284 V96 H274 Z")
    fill(p.accent, "M296,86 H306 V96 H296 Z")
}

private fun spot(name: String, p: ArtPalette, block: ImageVector.Builder.() -> Unit) = art(name, 64f, 64f) {
    fill(p.surface, circle(32f, 32f, 30f))
    block()
}

private fun buildIllustration(kind: Illustration, p: ArtPalette): ImageVector = when (kind) {
    Illustration.HERO -> heroVector(p)
    Illustration.CERTIFICATES -> spot("Certificates", p) {
        stroke(p.line, 2.5f, "M20,14 H38 L46,22 V50 H20 Z", fill = p.paper)
        stroke(p.line, 2.5f, "M38,14 V22 H46")
        stroke(p.line, 2.5f, "M25,29 H40 M25,35 H40 M25,41 H32")
        fill(p.accent, circle(42f, 45f, 7f))
        stroke(p.paper, 2.2f, "M38.6,45 L41,47.4 L45.4,42.6")
    }
    Illustration.EDUCATION -> spot("Education", p) {
        stroke(p.line, 3f, "M20,30 V38 Q32,45 44,38 V30", fill = p.paper)
        fill(p.line, "M32,16 L53,25 L32,34 L11,25 Z")
        stroke(p.accent, 2.5f, "M48,27 V37")
        fill(p.accent, circle(48f, 39f, 2.6f))
    }
    Illustration.EMPLOYMENT -> spot("Employment", p) {
        stroke(p.line, 3f, "M26,24 V20 A2,2 0 0 1 28,18 H36 A2,2 0 0 1 38,20 V24")
        stroke(p.line, 3f, "M17,24 H47 A3,3 0 0 1 50,27 V46 A3,3 0 0 1 47,49 H17 A3,3 0 0 1 14,46 V27 A3,3 0 0 1 17,24 Z", fill = p.paper)
        stroke(p.line, 2.5f, "M14,34 H50")
        fill(p.accent, "M29,31 H35 V37 H29 Z")
    }
    Illustration.PENSIONS -> spot("Pensions", p) {
        stroke(p.line, 2.5f, "M35,19 H51 V37 H35 Z", fill = p.paper)
        fill(p.line, "M35,19 H51 V25 H35 Z")
        stroke(p.line, 2.5f, "M39,15 V21 M47,15 V21")
        stroke(p.line, 2f, "M13,43 H35 V49 H13 Z", fill = p.accent)
        stroke(p.line, 2f, "M15,36 H37 V42 H15 Z", fill = p.accent)
        stroke(p.line, 2f, "M13,29 H35 V35 H13 Z", fill = p.accent)
    }
    Illustration.BUSINESS -> spot("Business", p) {
        stroke(p.line, 3f, "M17,29 H47 V50 H17 Z", fill = p.paper)
        stroke(p.line, 2.5f, "M13,20 H51 V29 H13 Z", fill = p.accent)
        stroke(p.line, 2f, "M22.5,20 V29 M32,20 V29 M41.5,20 V29")
        fill(p.line, "M27,37 H36 V50 H27 Z")
        fill(p.line, "M39,35 H44 V41 H39 Z")
    }
    Illustration.GRIEVANCES -> spot("Grievances", p) {
        stroke(p.line, 3f, "M18,16 H46 A4,4 0 0 1 50,20 V38 A4,4 0 0 1 46,42 H30 L21,50 V42 H18 A4,4 0 0 1 14,38 V20 A4,4 0 0 1 18,16 Z", fill = p.paper)
        stroke(p.line, 2.5f, "M21,25 H43 M21,32 H35")
        fill(p.accent, circle(42f, 32f, 3f))
    }
    Illustration.NO_RESULTS -> spot("NoResults", p) {
        stroke(p.line, 5f, "M37,37 L48,48")
        stroke(p.line, 3.5f, circle(28f, 28f, 12f), fill = p.paper)
        stroke(p.accent, 2.5f, "M24,25 Q28,19 32,24 Q32,28 28,30 V31")
        fill(p.accent, circle(28f, 35f, 1.8f))
    }
    Illustration.NO_APPLICATIONS -> spot("NoApplications", p) {
        stroke(p.line, 3f, "M18,14 H46 A3,3 0 0 1 49,17 V50 A3,3 0 0 1 46,53 H18 A3,3 0 0 1 15,50 V17 A3,3 0 0 1 18,14 Z", fill = p.paper)
        fill(p.line, "M25,10 H39 V18 H25 Z")
        stroke(p.line, 2.5f, "M22,28 H42 M22,35 H42 M22,42 H32")
        fill(p.accent, circle(44f, 46f, 8f))
        stroke(p.paper, 2.4f, "M44,42 V50 M40,46 H48")
    }
    Illustration.NO_SAVED -> spot("NoSaved", p) {
        stroke(p.line, 3f, "M32,48 C20,40 14,33 14,26 A9,9 0 0 1 32,22 A9,9 0 0 1 50,26 C50,33 44,40 32,48 Z", fill = p.paper)
        fill(p.accent, circle(46f, 18f, 3f))
    }
    Illustration.OFFLINE -> spot("Offline", p) {
        stroke(p.line, 3f, "M21,43 A8,8 0 0 1 21,27 A11,11 0 0 1 42,24 A9.5,9.5 0 0 1 45,43 Z", fill = p.paper)
        stroke(p.accent, 3.5f, "M17,49 L47,17")
    }
    Illustration.HELP -> spot("Help", p) {
        stroke(p.line, 3.5f, "M32,13 V52 M24,52 H40")
        stroke(p.line, 2.5f, "M32,17 H46 L51,22.5 L46,28 H32 Z", fill = p.accent)
        stroke(p.line, 2.5f, "M32,32 H18 L13,37.5 L18,43 H32 Z", fill = p.paper)
    }
}

/**
 * Renders an original JanSetu illustration. Pass [contentDescription] only
 * when the image carries information not given by nearby text; decorative
 * uses are removed from the accessibility tree.
 */
@Composable
fun JanSetuIllustration(kind: Illustration, modifier: Modifier = Modifier, contentDescription: String? = null) {
    val palette = artPalette()
    val vector = remember(kind, palette.surface, palette.line, palette.accent, palette.paper) { buildIllustration(kind, palette) }
    // Image adds no description or role when contentDescription is null, so
    // TalkBack skips decorative art. The tag exists only for UI tests.
    Image(vector, contentDescription, modifier.testTag("illustration:${kind.name.lowercase()}"))
}
