package ca.arnaud.horasolis.domain.model

import java.time.LocalDate
import java.time.LocalTime

object SolisDayTestData {

    val empty = SolisDay(
        atDate = LocalDate.of(2023, 10, 1),
        civilSunriseTime = LocalTime.of(6, 0),
        civilSunsetTime = LocalTime.of(18, 0),
        location = SavedLocationTestData.empty,
    )
}