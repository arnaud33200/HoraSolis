package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.model.SavedLocation

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(): SavedLocation? {
        return locationRepository.getCurrentLocationOrNull()
    }
}
