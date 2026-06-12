package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.repository.LocationRepository

class GetLocationOrNullUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(id: String): SavedLocation? {
        return locationRepository.getLocationOrNull(id)
    }
}
