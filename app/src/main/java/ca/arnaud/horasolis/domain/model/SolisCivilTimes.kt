package ca.arnaud.horasolis.domain.model

import java.time.Duration
import java.time.LocalDate

data class SolisCivilTimes(
    val location: SavedLocation,
    val date: LocalDate,
    val times: List<SolisCivilTime>,
    val dayDuration: Duration,
    val nightDuration: Duration,
)