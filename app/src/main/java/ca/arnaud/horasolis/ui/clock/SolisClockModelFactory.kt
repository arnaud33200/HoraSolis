package ca.arnaud.horasolis.ui.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.extension.format
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalTime

class SolisClockModelFactory {

    companion object {

        private const val SECONDS_IN_DAY = 86400
    }

    fun create(
        solisDay: SolisDay,
        atTime: SolisTime,
    ): SolisClockModel {
        val sunriseSeconds = solisDay.civilSunriseTime.toSecondOfDay().toLong()
        val sunsetSeconds = solisDay.civilSunsetTime.toSecondOfDay().toLong()
        val dayDuration =
            if (sunsetSeconds >= sunriseSeconds) sunsetSeconds - sunriseSeconds else SECONDS_IN_DAY - sunriseSeconds + sunsetSeconds
        val dayStartAngle = sunriseSeconds.secondsToClockAngle()
        val daySweepAngle = (dayDuration / SECONDS_IN_DAY.toFloat()) * 360f
        val nightStartAngle = (sunriseSeconds + dayDuration) % SECONDS_IN_DAY
        val nightStartAngleClock = nightStartAngle.secondsToClockAngle()
        val nightSweepAngle = 360f - daySweepAngle

        val needleAngle = when (atTime.type) {
            SolisTime.Type.Day -> {
                // Place needle within day arc
                dayStartAngle + ((atTime.hour - 1) + atTime.minute / 60f) * (daySweepAngle / 12f)
            }

            SolisTime.Type.Night -> {
                // Place needle within night arc
                nightStartAngleClock + ((atTime.hour - 1) + atTime.minute / 60f) * (nightSweepAngle / 12f)
            }
        }
        return SolisClockModel(
            dayStartAngle = dayStartAngle,
            dayEndAngle = daySweepAngle,
            needleAngle = needleAngle,
        )
    }

    private fun Long.secondsToClockAngle(): Float {
        return (this / SECONDS_IN_DAY.toFloat()) * 360f - 90f
    }
}

class SolisClockModelFactoryPreviewProvider :
    PreviewParameterProvider<SolisClockModelFactoryPreviewProvider.PreviewModel> {

    data class PreviewModel(
        val solisDay: SolisDay,
        val atTime: SolisTime,
    )

    override val values = sequenceOf(
        PreviewModel(
            solisDay = SolisDay(
                atDate = LocalDateTime.now().toLocalDate(),
                civilSunriseTime = LocalTime.of(6, 0),
                civilSunsetTime = LocalTime.of(18, 0)
            ),
            atTime = SolisTime(
                hour = 10,
                minute = 0,
                type = SolisTime.Type.Day,
            ),
        ),
        PreviewModel(
            solisDay = SolisDay(
                atDate = LocalDateTime.now().toLocalDate(),
                civilSunriseTime = LocalTime.of(6, 0),
                civilSunsetTime = LocalTime.of(18, 0)
            ),
            atTime = SolisTime(
                hour = 3,
                minute = 0,
                type = SolisTime.Type.Night,
            ),
        ),
        PreviewModel(
            solisDay = SolisDay(
                atDate = LocalDateTime.now().toLocalDate(),
                civilSunriseTime = LocalTime.of(5, 0),
                civilSunsetTime = LocalTime.of(19, 0)
            ),
            atTime = SolisTime(
                hour = 1,
                minute = 0,
                type = SolisTime.Type.Night,
            ),
        ),
        PreviewModel(
            solisDay = SolisDay(
                atDate = LocalDateTime.now().toLocalDate(),
                civilSunriseTime = LocalTime.of(8, 0),
                civilSunsetTime = LocalTime.of(17, 0)
            ),
            atTime = SolisTime(
                hour = 6,
                minute = 30,
                type = SolisTime.Type.Day,
            ),
        ),
    )
}

@PreviewLightDark
@Composable
fun SolisClockModelFactoryPreview(
    @PreviewParameter(SolisClockModelFactoryPreviewProvider::class) preview: SolisClockModelFactoryPreviewProvider.PreviewModel
) {
    val factory = SolisClockModelFactory()

    HoraSolisTheme {
        Surface {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var atTime by remember { mutableStateOf(preview.atTime) }
                Text(text = atTime.format())

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(100)
                        atTime = atTime.plusMinutes(10)
                    }
                }

                val model = factory.create(
                    solisDay = preview.solisDay,
                    atTime = atTime,
                )
                SolisClock(
                    model = model,
                    modifier = Modifier.size(300.dp)
                )
            }
        }
    }
}
