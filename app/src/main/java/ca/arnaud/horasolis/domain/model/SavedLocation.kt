package ca.arnaud.horasolis.domain.model

// TODO - rename SolisLocation and add Kdoc.
data class SavedLocation(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val timZoneId: String,
) {

    companion object {

        val empty = SavedLocation(
            id = "",
            name = "",
            lat = 0.0,
            lng = 0.0,
            timZoneId = "",
        )
    }
}
