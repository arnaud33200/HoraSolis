package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository

class SetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
) {

    suspend operator fun invoke(id: String) {
        if (locationRepository.getLocationOrNull(id) == null) return
        locationRepository.setCurrentLocation(id)
    }
}
