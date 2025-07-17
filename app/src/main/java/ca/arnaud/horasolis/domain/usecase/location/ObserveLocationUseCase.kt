package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

class ObserveLocationUseCase(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(): Flow<UserLocation?> = locationRepository.observeLocation()
}


