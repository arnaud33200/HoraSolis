package ca.arnaud.horasolis.domain.model

data class UserLocation(
    val lat: Double,
    val lng: Double,
    val timZoneId: String,
)
