package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.RomanTimeAlarmScheduleParam
import ca.arnaud.horasolis.RomanTimeAlarmService
import ca.arnaud.horasolis.data.HoraSolisDatabase

data class ScheduleNextDayAlarmParam(
    val number: Int,
)

class ScheduleNextDayAlarmUseCase(
    private val getRomanTimesUseCase: GetRomanTimesUseCase,
    private val alarmService: RomanTimeAlarmService,
    private val database: HoraSolisDatabase,
    private val timeProvider: TimeProvider,
) {

    suspend operator fun invoke(param: ScheduleNextDayAlarmParam) {
        val settingsDao = database.scheduleSettingsDao()
        val settings = settingsDao.getSettings() ?: return

        val params = GetRomanTimesParams(
            lat = settings.lat,
            lng = settings.lng,
            timZoneId = settings.timZoneId,
            date = timeProvider.getNowDate().plusDays(1),
        )
        val times = getRomanTimesUseCase(params)
        val timeToSchedule = times.getDataOrNull()?.times?.find {
            it.number == param.number
        } ?: return

        val alarmParams = RomanTimeAlarmScheduleParam(
            number = timeToSchedule.number,
            dateTime = timeProvider.getNowDate().plusDays(1).atTime(timeToSchedule.startTime)
        )
        alarmService.scheduleAlarm(alarmParams)
    }
}