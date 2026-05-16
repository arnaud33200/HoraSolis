package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.model.SavedLocation
import kotlinx.coroutines.flow.Flow

class ObserveLocationUseCase(
    private val locationRepository: LocationRepository,
) {

    operator fun invoke(): Flow<SavedLocation?> = locationRepository.observeLocation()
}
