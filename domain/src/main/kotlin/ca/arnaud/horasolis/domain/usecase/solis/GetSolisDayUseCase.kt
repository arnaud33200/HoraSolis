package ca.arnaud.horasolis.domain.usecase.solis

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.common.mapError
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.common.onFailure
import ca.arnaud.horasolis.domain.provider.Logger
import ca.arnaud.horasolis.domain.repository.LocationRepository
import ca.arnaud.horasolis.domain.repository.SolisRepository
import java.time.LocalDate

data class GetSolisDayParams(
    val location: SavedLocation,
    val date: LocalDate,
)

sealed interface GetSolisDayError {
    data object NoLocation : GetSolisDayError
    data class Unknown(val cause: Throwable) : GetSolisDayError
}

class GetSolisDayException(
    message: String,
    cause: Throwable? = null,
) : Exception("GetSolisDay failed: $message", cause)

class GetSolisDayUseCase(
    private val locationRepository: LocationRepository,
    private val solisRepository: SolisRepository,
    private val logger: Logger,
) {

    suspend operator fun invoke(
        atDate: LocalDate,
        atLocation: SavedLocation? = null,
    ): Response<SolisDay, GetSolisDayError> {
        return getSolisDay(atDate, atLocation).onFailure { error ->
            val cause = (error as? GetSolisDayError.Unknown)?.cause
            logger.logNonFatal(
                GetSolisDayException(
                    error::class.simpleName ?: error.toString(),
                    cause
                )
            )
        }
    }

    private suspend fun getSolisDay(
        atDate: LocalDate,
        atLocation: SavedLocation?,
    ): Response<SolisDay, GetSolisDayError> {
        val location = atLocation ?: locationRepository.getCurrentLocationOrNull()
        ?: return Response.Failure(GetSolisDayError.NoLocation)
        val params = GetSolisDayParams(
            location = location,
            date = atDate,
        )
        return solisRepository.getSolisDay(params).mapError { throwable ->
            GetSolisDayError.Unknown(throwable)
        }
    }
}
