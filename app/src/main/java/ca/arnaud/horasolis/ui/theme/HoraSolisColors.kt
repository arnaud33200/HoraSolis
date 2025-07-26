package ca.arnaud.horasolis.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color


@Immutable
sealed interface HoraSolisColors {
    val materialColorScheme: ColorScheme

    val daySurface: Color
    val onDaySurface: Color

    val nightSurface: Color
    val onNightSurface: Color

    val onDayNightSurface: Color
}

data class LightHoraSolisColors(
    override val materialColorScheme: ColorScheme = lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40
    ),

    override val daySurface: Color = Color(0xffecb858),
    override val onDaySurface: Color = Color(0xff2c230c),
    override val nightSurface: Color = Color(0xff0f324c),
    override val onNightSurface: Color = Color(0xffbbd8ff),
    override val onDayNightSurface: Color = Color(0xffffffff),
) : HoraSolisColors

data class DarkHoraSolisColors(
    override val materialColorScheme: ColorScheme = darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
    ),

    override val daySurface: Color = Color(0xffecb858),
    override val onDaySurface: Color = Color(0xff2c230c),
    override val nightSurface: Color = Color(0xff0f324c),
    override val onNightSurface: Color = Color(0xffbbd8ff),
    override val onDayNightSurface: Color = Color(0xffffffff),
) : HoraSolisColors
