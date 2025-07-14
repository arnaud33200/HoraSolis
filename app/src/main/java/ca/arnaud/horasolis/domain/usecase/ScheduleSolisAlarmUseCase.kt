package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.service.RomanTimeAlarmScheduleParam
import ca.arnaud.horasolis.service.RomanTimeAlarmService
import ca.arnaud.horasolis.domain.provider.TimeProvider
import java.time.LocalTime

class ScheduleSolisAlarmUseCase(
    private val alarmService: RomanTimeAlarmService,
    private val timeProvider: TimeProvider,
) {

    operator fun invoke(
        solisCivilTime: SolisCivilTime
    ) {
        return invoke(
            id = solisCivilTime.number,
            startTime = solisCivilTime.startTime
        )
    }

    operator fun invoke(
        id: Int,
        startTime: LocalTime,
    ) {
        /**
         * Add extra minutes to makes sure we don't schedule too early.
         * Fix for double schedule when alarm is ringing
         */
        val nowDateTime = timeProvider.getNowDateTime().plusMinutes(1)
        val atDate = if (startTime.isBefore(nowDateTime.toLocalTime())) {
            nowDateTime.toLocalDate().plusDays(1)
        } else {
            nowDateTime.toLocalDate()
        }
        val alarmParams = RomanTimeAlarmScheduleParam(
            number = id,
            dateTime = atDate.atTime(startTime)
        )
        alarmService.scheduleAlarm(alarmParams)
    }
}
