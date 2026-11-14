package `in`.jansetu.app.ui

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.jansetu.app.settings.LocalSettings
import `in`.jansetu.app.settings.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF0B5D66),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2EEF0),
    onPrimaryContainer = Color(0xFF073B42),
    secondary = Color(0xFF75592B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE8BF),
    onSecondaryContainer = Color(0xFF402D05),
    tertiary = Color(0xFF5A5C8D),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE1E0FF),
    onTertiaryContainer = Color(0xFF20214D),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFAFAF7),
    onBackground = Color(0xFF172829),
    surface = Color.White,
    surfaceVariant = Color(0xFFE4ECEB),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F6F4),
    surfaceContainer = Color(0xFFEDF1EF),
    surfaceContainerHigh = Color(0xFFE7ECEA),
    onSurface = Color(0xFF172829),
    onSurfaceVariant = Color(0xFF4B6264),
    outline = Color(0xFF7B9294),
    outlineVariant = Color(0xFFCAD9DA)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF91D5DB),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF174F56),
    onPrimaryContainer = Color(0xFFD2EEF0),
    secondary = Color(0xFFE8C77D),
    onSecondary = Color(0xFF422C00),
    secondaryContainer = Color(0xFF594516),
    onSecondaryContainer = Color(0xFFFFE9BE),
    tertiary = Color(0xFFC3C3FF),
    onTertiary = Color(0xFF2C2E59),
    tertiaryContainer = Color(0xFF434573),
    onTertiaryContainer = Color(0xFFE1E0FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF101919),
    onBackground = Color(0xFFE4EDEB),
    surface = Color(0xFF172222),
    surfaceVariant = Color(0xFF263535),
    surfaceContainerLowest = Color(0xFF0C1414),
    surfaceContainerLow = Color(0xFF1B2727),
    surfaceContainer = Color(0xFF22302F),
    surfaceContainerHigh = Color(0xFF2A3837),
    onSurface = Color(0xFFE4EDEB),
    onSurfaceVariant = Color(0xFFB9CACB),
    outline = Color(0xFF91A4A5),
    outlineVariant = Color(0xFF3E5051)
)

/** Tones outside Material's scheme. Every tone is paired with an icon and words. */
@Immutable
data class JanSetuExtraColors(
    val successContainer: Color, val onSuccessContainer: Color,
    val warningContainer: Color, val onWarningContainer: Color,
    /** Bridge path accent used only by the logo and illustrations. */
    val brandAccent: Color, val illustrationSurface: Color, val illustrationLine: Color
)

private val LightExtra = JanSetuExtraColors(
    successContainer = Color(0xFFDDF1E4), onSuccessContainer = Color(0xFF0E4F2C),
    warningContainer = Color(0xFFFCE8BF), onWarningContainer = Color(0xFF402D05),
    brandAccent = Color(0xFFA8700F), illustrationSurface = Color(0xFFE6F3F1), illustrationLine = Color(0xFF0B5D66)
)

private val DarkExtra = JanSetuExtraColors(
    successContainer = Color(0xFF1F4A33), onSuccessContainer = Color(0xFFC4EDD2),
    warningContainer = Color(0xFF594516), onWarningContainer = Color(0xFFFFE9BE),
    brandAccent = Color(0xFFE8B04A), illustrationSurface = Color(0xFF1E3132), illustrationLine = Color(0xFF91D5DB)
)

/** Spacing, widths and shapes shared by every screen. */
object JanSetuSpacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val xl = 20.dp
    val xxl = 28.dp
    /** Minimum touch target for custom clickable rows. Material components already enforce 48dp. */
    val touchTarget = 48.dp
}

private val JanSetuTypography = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 26.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = 22.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 21.sp, lineHeight = 27.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
)

private val JanSetuShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val LocalJanSetuMotion = staticCompositionLocalOf { JanSetuMotion(reduced = false) }
val LocalJanSetuColors = staticCompositionLocalOf { LightExtra }
val LocalJanSetuDark = staticCompositionLocalOf { false }

object JanSetuTheme {
    val colors: JanSetuExtraColors @Composable @ReadOnlyComposable get() = LocalJanSetuColors.current
    val motion: JanSetuMotion @Composable @ReadOnlyComposable get() = LocalJanSetuMotion.current
    val isDark: Boolean @Composable @ReadOnlyComposable get() = LocalJanSetuDark.current
}

@Composable
fun systemAnimationsDisabled(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        runCatching { Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }.getOrDefault(false)
    }
}

@Composable
fun resolveDarkTheme(mode: ThemeMode): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

/** App theme driven by the person's saved appearance, motion and text choices. */
@Composable
fun JanSetuTheme(settings: LocalSettings, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    // Only replace Android's density when the person asked for larger text, so
    // the platform's non-linear font scaling is otherwise left untouched.
    val override = if (settings.largerText) Density(density.density, LocalSettings.effectiveFontScale(density.fontScale, true)) else density
    CompositionLocalProvider(LocalDensity provides override) {
        JanSetuTheme(
            darkTheme = resolveDarkTheme(settings.themeMode),
            reducedMotion = settings.reduceMotion || systemAnimationsDisabled(),
            content = content
        )
    }
}

@Composable
fun JanSetuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalJanSetuColors provides if (darkTheme) DarkExtra else LightExtra,
        LocalJanSetuMotion provides JanSetuMotion(reducedMotion),
        LocalJanSetuDark provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = JanSetuTypography,
            shapes = JanSetuShapes,
            content = content
        )
    }
}
