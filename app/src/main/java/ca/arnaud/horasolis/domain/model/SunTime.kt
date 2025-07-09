package ca.arnaud.horasolis.domain.model

import java.time.LocalDate
import java.time.LocalTime

// TODO - rename to not be confused with [SolisTime]
data class SunTime(
    val date: LocalDate,
    val sunrise: LocalTime,
    val sunset: LocalTime,
)