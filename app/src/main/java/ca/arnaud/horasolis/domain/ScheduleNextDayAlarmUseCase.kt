package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.RomanTimeAlarmService

data class ScheduleNextDayAlarmParam(
    val number: Int,
)

class ScheduleNextDayAlarmUseCase(
    private val getRomanTimesUseCase: GetRomanTimesUseCase,
    private val alarmService: RomanTimeAlarmService,
) {

    suspend operator fun invoke(param: ScheduleNextDayAlarmParam) {
        /**
         * 1 - fetch current save settings to get lat, lng, timezone, ...
         * 2 - get sun times for next day (tomorrow)
         * 3 - call alarm service for the [ScheduleNextDayAlarmParam.number]
         */
    }
}