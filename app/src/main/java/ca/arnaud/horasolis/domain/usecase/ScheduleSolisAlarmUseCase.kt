package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.domain.model.SavedAlarm
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.service.RomanTimeAlarmScheduleParam
import ca.arnaud.horasolis.service.RomanTimeAlarmService
import java.time.LocalDate
import java.time.LocalTime

class ScheduleSolisAlarmUseCase(
    private val alarmService: RomanTimeAlarmService,
    private val timeProvider: TimeProvider,
    private val getSolisDay: GetSolisDayUseCase,
) {

    suspend operator fun invoke(
        savedAlarm: SavedAlarm,
        atDate: LocalDate,
    ) {
        if (!savedAlarm.enabled) {
            alarmService.cancelAlarm(savedAlarm.id)
            return
        }

        /**
         * Add extra minutes to makes sure we don't schedule too early.
         * Fix for double schedule when alarm is ringing
         */
        val nowDateTime = timeProvider.getNowDateTime().plusMinutes(1)
        val alarmDateTime = atDate.atTime(savedAlarm.toCivilTime(atDate))
        val atDateTime = if (alarmDateTime.isBefore(nowDateTime)) {
            val nextDayDate = atDate.plusDays(1)
            nextDayDate.atTime(savedAlarm.toCivilTime(nextDayDate))
        } else {
            alarmDateTime
        }
        val alarmParams = RomanTimeAlarmScheduleParam(
            number = savedAlarm.id,
            dateTime = atDateTime,
        )
        alarmService.scheduleAlarm(alarmParams)
    }

    private suspend fun SavedAlarm.toCivilTime(atDate: LocalDate): LocalTime {
        val solisDay = getSolisDay(atDate).getDataOrNull()
            ?: SolisDay(
                // TODO - setup an error
                atDate = atDate,
                civilSunriseTime = LocalTime.of(6, 0),
                civilSunsetTime = LocalTime.of(18, 0),
            )
        return solisTime.toCivilTime(solisDay)
    }
}
