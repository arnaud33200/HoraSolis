package ca.arnaud.horasolis.domain.model.location

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.common.UpdateParam
import ca.arnaud.horasolis.domain.model.common.UpdateParam.Unchanged.getUpdateDataOrDefault

data class LocationUpdateParams(
    val name: UpdateParam<String> = UpdateParam.Unchanged,
    val lat: UpdateParam<Double> = UpdateParam.Unchanged,
    val lng: UpdateParam<Double> = UpdateParam.Unchanged,
    val timZoneId: UpdateParam<String> = UpdateParam.Unchanged,
)

fun SavedLocation.applyUpdates(updateParams: LocationUpdateParams): SavedLocation {
    return SavedLocation(
        id = id,
        name = updateParams.name.getUpdateDataOrDefault(name),
        lat = updateParams.lat.getUpdateDataOrDefault(lat),
        lng = updateParams.lng.getUpdateDataOrDefault(lng),
        timZoneId = updateParams.timZoneId.getUpdateDataOrDefault(timZoneId),
    )
}