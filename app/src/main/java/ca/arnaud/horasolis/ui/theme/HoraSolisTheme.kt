package ca.arnaud.horasolis.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppColors = staticCompositionLocalOf<HoraSolisColors> { LightHoraSolisColors() }

object HoraSolisTheme {

    val colors: HoraSolisColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

@Composable
fun HoraSolisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val horaSolisColors: HoraSolisColors = when {
        // TODO - support dynamic colors #9
        darkTheme -> DarkHoraSolisColors()
        else -> LightHoraSolisColors()
    }

    CompositionLocalProvider(
        LocalAppColors provides horaSolisColors,
    ) {
        MaterialTheme(
            colorScheme = horaSolisColors.materialColorScheme,
            typography = Typography,
            content = content
        )
    }
}