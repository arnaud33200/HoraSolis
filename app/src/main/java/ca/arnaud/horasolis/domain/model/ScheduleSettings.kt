package ca.arnaud.horasolis.domain.model

import ca.arnaud.horasolis.domain.model.SolisCivilTime

data class ScheduleSettings(
    val location: UserLocation,
    val selectedTime: List<SolisCivilTime>,
)