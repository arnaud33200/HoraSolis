package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.data.SolisRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.mapError
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.SolisDay
import java.time.LocalDate

data class GetSolisDayParams(
    val location: SavedLocation,
    val date: LocalDate,
)

enum class GetSolisDayError {
    NoLocation,
    Unknown,
}

class GetSolisDayUseCase(
    private val locationRepository: LocationRepository,
    private val solisRepository: SolisRepository,
) {

    suspend operator fun invoke(
        atDate: LocalDate,
    ): Response<SolisDay, GetSolisDayError> {
        val location = locationRepository.getCurrentLocationOrNull()
            ?: return Response.Failure(GetSolisDayError.NoLocation)
        val params = GetSolisDayParams(
            location = location,
            date = atDate,
        )
        return solisRepository.getSolisDay(params).mapError {
            GetSolisDayError.Unknown
        }
    }
}
