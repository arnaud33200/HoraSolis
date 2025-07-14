package ca.arnaud.horasolis.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class SolisDay(
    val atDate: LocalDate,
    val civilSunriseTime: LocalTime,
    val civilSunsetTime: LocalTime,
)
