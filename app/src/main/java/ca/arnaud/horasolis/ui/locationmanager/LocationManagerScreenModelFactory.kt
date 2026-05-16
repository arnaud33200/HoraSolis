package ca.arnaud.horasolis.ui.locationmanager

import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.collections.immutable.toImmutableList

class LocationManagerScreenModelFactory(
    private val stringProvider: StringProvider,
) {

    fun create(
        allLocations: List<SavedLocation>,
        currentLocation: SavedLocation?,
    ): LocationManagerScreenModel {
        if (allLocations.isEmpty()) return LocationManagerScreenModel.Empty

        val currentLocationState = if (currentLocation != null) {
            val item = allLocations
                .firstOrNull { it.isCurrentLocation(currentLocation) }
                ?.toItemModel()
                ?: LocationItemModel(
                    id = currentLocation.id,
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

    private fun SavedLocation.isCurrentLocation(currentLocation: SavedLocation?): Boolean {
        return id == currentLocation?.id
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
