package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.local.HoraSolisDatabase

data class ScheduleNextDayAlarmParam(
    val number: Int,
)

class ScheduleNextDayAlarmUseCase(
    private val getRomanTimesUseCase: GetRomanTimesUseCase,
    private val scheduleRomanTime: ScheduleRomanTimeUseCase,
    private val database: HoraSolisDatabase,
    private val timeProvider: TimeProvider,
) {

    // TODO - return a Response
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

        scheduleRomanTime(timeToSchedule)

    }
}