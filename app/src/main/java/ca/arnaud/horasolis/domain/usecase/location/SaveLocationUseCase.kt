package ca.arnaud.horasolis.domain.usecase.location

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.location.LocationUpdateParams
import ca.arnaud.horasolis.domain.model.location.applyUpdates
import ca.arnaud.horasolis.domain.provider.LocaleProvider
import java.util.UUID

sealed interface SaveLocationParams {

    val updateParams: LocationUpdateParams

    data class New(
        override val updateParams: LocationUpdateParams,
    ) : SaveLocationParams

    data class Edit(
        val id: String,
        override val updateParams: LocationUpdateParams,
    ) : SaveLocationParams
}

class SaveLocationUseCase(
    private val locationRepository: LocationRepository,
    private val localeProvider: LocaleProvider,
) {

    suspend operator fun invoke(params: SaveLocationParams) {
        val location = when (params) {
            is SaveLocationParams.New -> SavedLocation.empty.copy(
                id = UUID.randomUUID().toString()
            )

            is SaveLocationParams.Edit -> locationRepository.getLocationOrNull(params.id)
                ?: SavedLocation.empty
        }.applyUpdates(
            updateParams = params.updateParams
        )
        locationRepository.saveLocation(
            location.copy(
                timZoneId = location.timZoneId.ifBlank { localeProvider.getTimezoneId() },
            )
        )
        if (locationRepository.getCurrentLocation() == null) {
            locationRepository.setCurrentLocation(location.id)
        }
    }
}
