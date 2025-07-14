package ca.arnaud.horasolis.domain.usecase

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
    private val solisRepository: SolisRepository,
) {

    suspend operator fun invoke(
        params: GetSolisDayParams,
    ): Response<SolisDay, Throwable> {
        return solisRepository.getSolisDay(params)
    }
}
