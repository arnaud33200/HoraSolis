package ca.arnaud.horasolis.domain.model

// TODO - rename SolisLocation and add Kdoc.
data class SavedLocation(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val timZoneId: String,
)
