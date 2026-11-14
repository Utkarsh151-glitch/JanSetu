package `in`.jansetu.app.ui

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable

/**
 * Motion policy. Decorative motion is off when the person chose Reduce motion
 * or Android's animator scale is zero. Functional transitions still use
 * Compose's default handling of the Android animator scale.
 */
@Immutable
data class JanSetuMotion(val reduced: Boolean) {
    fun <T> standard(durationMillis: Int = 220): FiniteAnimationSpec<T> = motionSpec(reduced, durationMillis)
}

fun <T> motionSpec(reduced: Boolean, durationMillis: Int = 220): FiniteAnimationSpec<T> =
    if (reduced) snap() else tween(durationMillis)

