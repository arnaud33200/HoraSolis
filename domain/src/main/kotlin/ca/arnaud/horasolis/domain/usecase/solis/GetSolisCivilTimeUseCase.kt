package ca.arnaud.horasolis.domain.usecase.solis

import ca.arnaud.horasolis.domain.model.common.Response
import ca.arnaud.horasolis.domain.model.common.map
import ca.arnaud.horasolis.domain.model.SolisCivilTime
import ca.arnaud.horasolis.domain.model.SolisCivilTimes
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.common.onFailure
import ca.arnaud.horasolis.domain.provider.Logger
import ca.arnaud.horasolis.domain.repository.SolisRepository
import java.time.Duration
import java.time.LocalTime

class GetSolisCivilTimeException(cause: Throwable) : Exception("GetSolisCivilTime failed", cause)

class GetSolisCivilTimeUseCase(
    private val solisRepository: SolisRepository,
    private val logger: Logger,
) {

    companion object {

        private const val DAY_HOUR_COUNT = 12
        private const val NIGHT_HOUR_COUNT = 12
    }

    suspend operator fun invoke(
        params: GetSolisDayParams,
    ): Response<SolisCivilTimes, Throwable> {
        return solisRepository.getSolisDay(params).map(
            transform = { solisDay ->
                calculateSolisCivilTimes(solisDay, params)
            }
        ).onFailure { throwable ->
            logger.logNonFatal(GetSolisCivilTimeException(throwable))
        }
    }

    /**
     * TODO - handle edge case for when sunset and sunrise times are the same (ex: polar day/night).
     *  In this case, we need to have the `astronomicalTwilight` (end & start) in [solisDay]
     *  as May 16, 2026, it would always default to be night all the time.
     */
    private fun calculateSolisCivilTimes(
        solisDay: SolisDay,
        params: GetSolisDayParams,
    ): SolisCivilTimes {
        val dayDuration = solisDay.dayDuration
        val dayTimes = solisDay.civilSunriseTime
            .toSolisCivilDayTimes(solisDay.civilSunsetTime)
            .toSolisCivilTimes(dayDuration, SolisCivilTime.Type.Day)

        val nightDuration = solisDay.nightDuration
        val nightTimes = solisDay.civilSunsetTime
            .toSolisCivilNightTimes(nightDuration)
            .toSolisCivilTimes(nightDuration, SolisCivilTime.Type.Night)

        return SolisCivilTimes(
            date = solisDay.atDate,
            times = dayTimes + nightTimes,
            location = params.location,
            dayDuration = dayDuration,
            nightDuration = nightDuration,
        )
    }

    private fun LocalTime.toSolisCivilDayTimes(
        sunsetTime: LocalTime
    ): List<LocalTime> {
        val duration = Duration.between(this, sunsetTime)
        val unitDuration = duration.dividedBy(DAY_HOUR_COUNT.toLong())
        return List(DAY_HOUR_COUNT) { index ->
            this.plus(unitDuration.multipliedBy(index.toLong()))
        }
    }

    private fun LocalTime.toSolisCivilNightTimes(
        nightDuration: Duration
    ): List<LocalTime> {
        val unitDuration = nightDuration.dividedBy(NIGHT_HOUR_COUNT.toLong())
        return List(NIGHT_HOUR_COUNT) { index ->
            this.plus(unitDuration.multipliedBy(index.toLong()))
        }
    }

    private fun List<LocalTime>.toSolisCivilTimes(
        duration: Duration,
        type: SolisCivilTime.Type,
    ): List<SolisCivilTime> {
        val count = if (type == SolisCivilTime.Type.Day) DAY_HOUR_COUNT else NIGHT_HOUR_COUNT
        val timeDuration = duration.dividedBy(count.toLong())
        val offsetIndex = if (type == SolisCivilTime.Type.Day) 0 else DAY_HOUR_COUNT
        return this.mapIndexed { index, time ->
            SolisCivilTime(
                number = index + 1 + offsetIndex,
                startTime = time,
                duration = timeDuration,
                type = type,
            )
        }
    }
}
