package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.model.SavedLocation

class GetLocationOrNullUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(id: String): SavedLocation? {
        return locationRepository.getLocationOrNull(id)
    }
}
