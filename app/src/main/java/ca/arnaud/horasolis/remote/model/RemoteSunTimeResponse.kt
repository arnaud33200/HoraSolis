package ca.arnaud.horasolis.remote.model

import ca.arnaud.horasolis.domain.model.SunTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class RemoteSunTimeResponse(
    val results: Result,
) {

    @Serializable
    data class Result(
        @Contextual
        val sunrise: LocalTime,
        @Contextual
        val sunset: LocalTime,
    )

    fun toSunTime(atDate: LocalDate): SunTime {
        return SunTime(
            date = atDate,
            sunrise = results.sunrise,
            sunset = results.sunset,
        )
    }
}