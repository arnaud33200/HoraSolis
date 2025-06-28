package ca.arnaud.horasolis.domain.model

data class ScheduleSettings(
    val location: UserLocation,
    val selectedTime: List<RomanTime>,
)