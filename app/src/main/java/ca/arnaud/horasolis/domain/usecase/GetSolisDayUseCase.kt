package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.data.SolisRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.UserLocation
import java.time.LocalDate

data class GetSolisDayParams(
    val location: UserLocation,
    val date: LocalDate,
)

class GetSolisDayUseCase(
    private val locationRepository: LocationRepository,
    private val solisRepository: SolisRepository,
) {

    suspend operator fun invoke(
        atDate: LocalDate,
    ): Response<SolisDay, Throwable> {
        val location = locationRepository.getCurrentLocation()
            ?: return Response.Failure(Throwable("No location available"))
        val params = GetSolisDayParams(
            location = location,
            date = atDate,
        )
        return solisRepository.getSolisDay(params)
    }
}
