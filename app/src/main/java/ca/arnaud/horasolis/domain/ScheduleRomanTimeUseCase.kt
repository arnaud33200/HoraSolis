package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.RomanTimeAlarmScheduleParam
import ca.arnaud.horasolis.RomanTimeAlarmService

class ScheduleRomanTimeUseCase(
    private val alarmService: RomanTimeAlarmService,
    private val timeProvider: TimeProvider,
) {

    operator fun invoke(time: RomanTime) {
        /**
         * Add extra minutes to makes sure we don't schedule too early.
         * Fix for double schedule when alarm is ringing
         */
        val nowDateTime = timeProvider.getNowDateTime().plusMinutes(1)
        val atDate = if (time.startTime.isBefore(nowDateTime.toLocalTime())) {
            nowDateTime.toLocalDate().plusDays(1)
        } else {
            nowDateTime.toLocalDate()
        }
        val alarmParams = RomanTimeAlarmScheduleParam(
            number = time.number,
            dateTime = atDate.atTime(time.startTime)
        )
        alarmService.scheduleAlarm(alarmParams)
    }
}
