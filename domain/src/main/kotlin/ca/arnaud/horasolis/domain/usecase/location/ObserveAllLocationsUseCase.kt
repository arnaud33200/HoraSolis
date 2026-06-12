package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllLocationsUseCase(
    private val locationRepository: LocationRepository,
) {

    operator fun invoke(): Flow<List<SavedLocation>> = locationRepository.observeAllLocations()
}
