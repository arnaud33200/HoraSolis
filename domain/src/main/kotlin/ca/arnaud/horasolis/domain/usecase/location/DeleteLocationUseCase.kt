package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.common.onFailure
import ca.arnaud.horasolis.domain.provider.Logger
import ca.arnaud.horasolis.domain.repository.LocationRepository

enum class DeleteLocationError {
    LastLocation,
}

class DeleteLocationException(message: String) : Exception("DeleteLocation failed: $message")

class DeleteLocationUseCase(
    private val locationRepository: LocationRepository,
    private val logger: Logger,
) {

    suspend operator fun invoke(id: String): Response<Unit, DeleteLocationError> {
        return deleteLocation(id).onFailure { error ->
            logger.logNonFatal(DeleteLocationException(error.name))
        }
    }

    private suspend fun deleteLocation(id: String): Response<Unit, DeleteLocationError> {
        val allLocations = locationRepository.getAllLocations()
        if (allLocations.size <= 1) {
            return Response.Failure(DeleteLocationError.LastLocation)
        }

        val currentLocation = locationRepository.getCurrentLocationOrNull()
        locationRepository.deleteLocation(id)

        if (currentLocation?.id == id) {
            allLocations.firstOrNull { it.id != id }?.let { location ->
                locationRepository.setCurrentLocation(location.id)
            }
        }
        return Response.Success(Unit)
    }
}
