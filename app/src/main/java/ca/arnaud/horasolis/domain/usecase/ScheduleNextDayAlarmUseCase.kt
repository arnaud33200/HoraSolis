package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.ScheduleSettingsRepository
import ca.arnaud.horasolis.domain.provider.TimeProvider

data class ScheduleNextDayAlarmParam(
    val number: Int,
)

class ScheduleNextDayAlarmUseCase(
    private val getRomanTimesUseCase: GetRomanTimesUseCase,
    private val scheduleRomanTime: ScheduleRomanTimeUseCase,
    private val scheduleSettingsRepository: ScheduleSettingsRepository,
    private val timeProvider: TimeProvider,
) {

    // TODO - return a Response (#8)
    suspend operator fun invoke(param: ScheduleNextDayAlarmParam) {
        val settings = scheduleSettingsRepository.getScheduleSettingsOrNull() ?: return

        val params = GetRomanTimesParams(
            location = settings.location,
            date = timeProvider.getNowDate().plusDays(1),
        )
        val times = getRomanTimesUseCase(params)
        val timeToSchedule = times.getDataOrNull()?.times?.find {
            it.number == param.number
        } ?: return

        scheduleRomanTime(timeToSchedule)

    }
}