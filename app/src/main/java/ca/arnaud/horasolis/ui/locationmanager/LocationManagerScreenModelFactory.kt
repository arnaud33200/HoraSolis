package ca.arnaud.horasolis.ui.locationmanager

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.toImmutableList

class LocationManagerScreenModelFactory(
    private val stringProvider: StringProvider,
) {

    fun create(
        allLocations: List<SavedLocation>,
        currentLocation: UserLocation?,
    ): LocationManagerScreenModel {
        if (allLocations.isEmpty()) return LocationManagerScreenModel.Empty

        val currentLocationState = if (currentLocation != null) {
            val item = allLocations
                .firstOrNull { it.isCurrentLocation(currentLocation) }
                ?.toItemModel()
                ?: LocationItemModel(
                    id = "current",
                    name = stringProvider.getString(
                        R.string.location_manager_item_default_name,
                        "?"
                    ),
                    locationInfo = "${currentLocation.lng} / ${currentLocation.lat}",
                )
            CurrentLocationState.Location(item)
        } else {
            CurrentLocationState.SelectLocation
        }

        val savedLocations = allLocations.filter { savedLocation ->
            savedLocation.isCurrentLocation(currentLocation).not()
        }.map { savedLocation ->
        savedLocation.toItemModel()
        }.toImmutableList()

        return LocationManagerScreenModel.Content(
            currentLocationState = currentLocationState,
            savedLocations = savedLocations,
        )
    }

    fun SavedLocation.isCurrentLocation(currentLocation: UserLocation?): Boolean {
        if (currentLocation == null) return false
        return lat == currentLocation.lat && lng == currentLocation.lng
    }

    private fun SavedLocation.toItemModel() = LocationItemModel(
        id = id,
        name = name.ifBlank {
            stringProvider.getString(
                R.string.location_manager_item_default_name, id,
            )
        },
        locationInfo = "$lng / $lat",
    )
}
