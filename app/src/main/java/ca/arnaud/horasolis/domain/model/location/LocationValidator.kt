package ca.arnaud.horasolis.domain.model.location

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.common.UpdateParam

class LocationValidator {

    fun isValid(
        updateParams: LocationUpdateParams,
        initialLocation: SavedLocation,
    ): Boolean {
        val name = (updateParams.name as? UpdateParam.Update)?.data ?: initialLocation.name
        val lat = (updateParams.lat as? UpdateParam.Update)?.data
        val lng = (updateParams.lng as? UpdateParam.Update)?.data
        return name.isNotBlank() && lat != null && lng != null
    }
}
