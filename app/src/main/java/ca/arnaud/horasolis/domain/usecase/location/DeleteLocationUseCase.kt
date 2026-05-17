package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.Response

enum class DeleteLocationError {
    LastLocation,
}

class DeleteLocationUseCase(
    private val locationRepository: LocationRepository,
) {

    suspend operator fun invoke(id: String): Response<Unit, DeleteLocationError> {
        val allLocations = locationRepository.getAllLocations()
        if (allLocations.size <= 1) {
            return Response.Failure(DeleteLocationError.LastLocation)
        }

        val currentLocation = locationRepository.getCurrentLocation()
        locationRepository.deleteLocation(id)

        if (currentLocation?.id == id) {
            allLocations.firstOrNull { it.id != id }?.let { location ->
                locationRepository.setCurrentLocation(location.id)
            }
        }
        return Response.Success(Unit)
    }
}
