package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.model.UserLocation
import ca.arnaud.horasolis.domain.provider.TimeProvider

data class SetCurrentLocationParams(
    val lat: Double,
    val long: Double,
)

class SetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
    private val timeProvider: TimeProvider,
) {

    suspend operator fun invoke(params: SetCurrentLocationParams) {
        val userLocation = UserLocation(
            lat = params.lat,
            lng = params.long,
            timZoneId = timeProvider.getZoneId().id,
        )
        locationRepository.setCurrentLocation(userLocation)
    }
}