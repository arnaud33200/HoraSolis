package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.flatMap
import ca.arnaud.horasolis.domain.map
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.service.SolisTimeAlarmScheduleParam
import ca.arnaud.horasolis.service.SolisTimeAlarmService
import java.time.LocalDate
import java.time.LocalTime

enum class ScheduleSolisAlarmError {

    /**
     * Couldn't find the alarm with a specific id.
     * May happen when alarm is removed after being scheduled.
     */
    SavedAlarmNotFound,

    /**
     * Alarm is not enabled, alarm cannot be scheduled.
     */
    NotEnabled,

    /**
     * Alarm is enabled but there is no day in the week to schedule for.
     */
    EmptyWeekDays,

    /**
     * Couldn't get the solis day for a specific date.
     * This is required to convert solis time to civil time.
     */
    MissingSolisDay,
}

/**
 * Schedules the next alarm time for a given alarm.
 * Used when add/edit an alarm, and used after an alarm is ringing to schedule the next one.
 *
 * The next alarm can be either this week or next week, depending on the time and schedule.
 * Alarm is canceled if not enabled or if there is no more day in the week to schedule for.
 */
class ScheduleNextAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val alarmService: SolisTimeAlarmService,
    private val timeProvider: TimeProvider,
    private val getSolisDay: GetSolisDayUseCase,
) {

    companion object {

        private const val NUMBER_OF_DAYS_IN_WEEK = 7
    }

    suspend operator fun invoke(
        alarmId: Int,
    ): Response<Unit, ScheduleSolisAlarmError> {
        val alarm = alarmRepository.getAlarmOrNull(alarmId)
            ?: return Response.Failure(ScheduleSolisAlarmError.SavedAlarmNotFound)
        return invoke(alarm)
    }

    suspend operator fun invoke(
        savedAlarm: SavedAlarm,
    ): Response<Unit, ScheduleSolisAlarmError> {
        if (!savedAlarm.enabled) {
            return cancelAlarmWithError(
                savedAlarm = savedAlarm,
                error = ScheduleSolisAlarmError.NotEnabled,
            )
        }

        /**
         * Add extra minutes to makes sure we don't schedule too early.
         * Fix for double schedule when alarm is ringing
         */
        val nowDateTime = timeProvider.getNowDateTime().plusMinutes(1)
        val alarmNowTime = savedAlarm.toCivilTime(timeProvider.getNowDate()).flatMap(
            transform = { it },
            transformError = { return Response.Failure(it) },
        )

        val nowDayOfWeek = nowDateTime.dayOfWeek
        val nextAlarmDayOfWeek = savedAlarm.onForWeekDays.firstOrNull {
            if (alarmNowTime.isBefore(nowDateTime.toLocalTime())) {
                it.ordinal > nowDayOfWeek.ordinal
            } else {
                it.ordinal >= nowDayOfWeek.ordinal
            }
        }

        val atDate = when {
            nextAlarmDayOfWeek == null -> {
                // No more day in this week, schedule for the first day in next week
                val nextWeekDay = savedAlarm.onForWeekDays.firstOrNull()
                    ?: return cancelAlarmWithError(
                        savedAlarm = savedAlarm,
                        error = ScheduleSolisAlarmError.EmptyWeekDays,
                    )

                val daysCount = NUMBER_OF_DAYS_IN_WEEK - nowDayOfWeek.ordinal + nextWeekDay.ordinal
                val daysUntilNextAlarm = daysCount % (NUMBER_OF_DAYS_IN_WEEK + 1)
                nowDateTime.plusDays(daysUntilNextAlarm.toLong())
            }

            else -> {
                val numberOfDays = nextAlarmDayOfWeek.ordinal - nowDayOfWeek.ordinal
                nowDateTime.plusDays(numberOfDays.toLong())
            }
        }.toLocalDate()


        val alarmTime = savedAlarm.toCivilTime(atDate).flatMap(
            transform = { it },
            transformError = { return Response.Failure(it) },
        )
        val alarmParams = SolisTimeAlarmScheduleParam(
            alarmId = savedAlarm.id,
            dateTime = atDate.atTime(alarmTime),
        )
        alarmService.scheduleAlarm(alarmParams)
        return Response.Success(Unit)
    }

    private fun cancelAlarmWithError(
        savedAlarm: SavedAlarm,
        error: ScheduleSolisAlarmError,
    ): Response<Unit, ScheduleSolisAlarmError> {
        alarmService.cancelAlarm(savedAlarm.id)
        return Response.Failure(error)
    }


    private suspend fun SavedAlarm.toCivilTime(
        atDate: LocalDate,
    ): Response<LocalTime, ScheduleSolisAlarmError> {
        return getSolisDay(atDate).map(
            transform = { solisDay -> solisTime.toCivilTime(solisDay) },
            transformError = { ScheduleSolisAlarmError.MissingSolisDay },
        )
    }
}