package `in`.jansetu.app.settings

import android.content.Context
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray

/** Appearance choice. SYSTEM follows Android's dark-theme setting. */
enum class ThemeMode(val key: String) {
    SYSTEM("system"), LIGHT("light"), DARK("dark");
    companion object { fun fromKey(value: String?) = entries.firstOrNull { it.key == value } ?: SYSTEM }
}

/**
 * On-device preferences only. No account, analytics, or cloud synchronization.
 * Values are written only when the person changes them; reading preferences at
 * launch never rewrites the file.
 */
class LocalSettings(context: Context) {
    private val preferences = context.getSharedPreferences("jansetu_preferences", Context.MODE_PRIVATE)

    var onboardingComplete by mutableStateOf(preferences.getBoolean("onboarding_complete", false))
        private set
    var language by mutableStateOf(preferences.getString("language", "en") ?: "en")
        private set
    var state by mutableStateOf(preferences.getString("state", "All India") ?: "All India")
        private set
    var favorites by mutableStateOf(preferences.getStringSet("favorites", emptySet())?.toSet() ?: emptySet())
        private set
    var recent by mutableStateOf(preferences.getString("recent", "")?.split(",")?.filter { it.isNotBlank() } ?: emptyList())
        private set
    var recentSearches by mutableStateOf(runCatching {
        val values = JSONArray(preferences.getString("recent_searches", "[]"))
        (0 until values.length()).map { values.getString(it) }.take(8)
    }.getOrDefault(emptyList()))
        private set
    var themeMode by mutableStateOf(ThemeMode.fromKey(preferences.getString("theme_mode", null)))
        private set
    /** Turns off optional decorative motion. Android's own animation scale is always respected. */
    var reduceMotion by mutableStateOf(preferences.getBoolean("reduce_motion", false))
        private set
    /** Enlarges JanSetu's own text on top of the Android font size, capped at 2x overall. */
    var largerText by mutableStateOf(preferences.getBoolean("larger_text", false))
        private set

    fun finishOnboarding() {
        onboardingComplete = true
        preferences.edit { putBoolean("onboarding_complete", true) }
    }

    fun updateLanguage(value: String) {
        require(value == "en" || value == "hi")
        language = value
        preferences.edit { putString("language", value) }
    }

    fun updateState(value: String) {
        state = value
        preferences.edit { putString("state", value) }
    }

    fun updateThemeMode(value: ThemeMode) {
        themeMode = value
        preferences.edit { putString("theme_mode", value.key) }
    }

    fun updateReduceMotion(value: Boolean) {
        reduceMotion = value
        preferences.edit { putBoolean("reduce_motion", value) }
    }

    fun updateLargerText(value: Boolean) {
        largerText = value
        preferences.edit { putBoolean("larger_text", value) }
    }

    fun toggleFavorite(id: String) {
        favorites = if (id in favorites) favorites - id else favorites + id
        preferences.edit { putStringSet("favorites", favorites) }
    }

    fun recordViewed(id: String) {
        recent = (listOf(id) + recent.filterNot { it == id }).take(10)
        preferences.edit { putString("recent", recent.joinToString(",")) }
    }

    fun recordSearch(query: String) {
        val cleaned = query.trim().take(120)
        if (cleaned.length < 2) return
        recentSearches = (listOf(cleaned) + recentSearches.filterNot { it.equals(cleaned, ignoreCase = true) }).take(8)
        preferences.edit { putString("recent_searches", JSONArray(recentSearches).toString()) }
    }

    fun clearHistory() {
        recent = emptyList()
        recentSearches = emptyList()
        preferences.edit { remove("recent"); remove("recent_searches") }
    }

    companion object {
        /** JanSetu's own text multiplier when [largerText] is on. */
        const val LARGER_TEXT_FACTOR = 1.15f

        fun effectiveFontScale(system: Float, largerText: Boolean): Float =
            if (!largerText) system else maxOf(system, minOf(system * LARGER_TEXT_FACTOR, 2f))
    }
}
