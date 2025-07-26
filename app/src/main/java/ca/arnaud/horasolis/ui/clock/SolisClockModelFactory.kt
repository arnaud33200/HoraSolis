package ca.arnaud.horasolis.ui.clock

import ca.arnaud.horasolis.domain.model.SolisDay
import java.time.LocalTime

object SolisClockModelFactory {
    private const val SECONDS_IN_DAY = 86400

    fun create(solisDay: SolisDay): SolisClockModel {
        val sunriseSeconds = solisDay.civilSunriseTime.toSecondOfDay()
        val sunsetSeconds = solisDay.civilSunsetTime.toSecondOfDay()
        val dayDuration = if (sunsetSeconds >= sunriseSeconds) sunsetSeconds - sunriseSeconds else SECONDS_IN_DAY - sunriseSeconds + sunsetSeconds
        val daySweep = (dayDuration / SECONDS_IN_DAY.toFloat()) * 360f
        val dayStartAngle = (sunriseSeconds / SECONDS_IN_DAY.toFloat()) * 360f - 90f
        return SolisClockModel(
            dayStartAngle = dayStartAngle,
            daySweepAngle = daySweep
        )
    }
}
