package ca.arnaud.horasolis.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures

/**
 * Triggers [onQuickClicks] when the composable is tapped [count] times in quick succession.
 * Each tap must occur within [windowMs] milliseconds of the previous one, otherwise the counter resets.
 */
fun Modifier.onQuickClicks(
    count: Int,
    windowMs: Long = 500L,
    onQuickClicks: () -> Unit,
): Modifier = composed {
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    pointerInput(onQuickClicks) {
        detectTapGestures {
            val now = System.currentTimeMillis()
            tapCount = if (now - lastTapTime > windowMs) 1 else tapCount + 1
            lastTapTime = now
            if (tapCount >= count) {
                tapCount = 0
                onQuickClicks()
            }
        }
    }
}
