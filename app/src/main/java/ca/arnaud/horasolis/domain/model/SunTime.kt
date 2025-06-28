package ca.arnaud.horasolis.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class SunTime(
    val date: LocalDate,
    val sunrise: LocalTime,
    val sunset: LocalTime,
)