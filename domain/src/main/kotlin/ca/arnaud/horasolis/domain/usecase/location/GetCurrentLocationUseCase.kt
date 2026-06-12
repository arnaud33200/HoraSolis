package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.repository.LocationRepository

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(): SavedLocation? {
        return locationRepository.getCurrentLocationOrNull()
    }
}
