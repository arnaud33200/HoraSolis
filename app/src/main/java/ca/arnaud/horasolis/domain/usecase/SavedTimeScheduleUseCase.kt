package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.RomanTimeAlarmService
import ca.arnaud.horasolis.data.ScheduleSettingsRepository
import ca.arnaud.horasolis.domain.model.RomanTime
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.model.UserLocation

data class SavedTimeScheduleParams(
    val location: UserLocation,
    val times: List<RomanTime>,
)

class SavedTimeScheduleUseCase(
    private val scheduleSettingsRepository: ScheduleSettingsRepository,
    private val scheduleRomanTime: ScheduleRomanTimeUseCase,
    private val alarmService: RomanTimeAlarmService,
) {

    suspend operator fun invoke(scheduleSettings: ScheduleSettings) {
        scheduleSettingsRepository.saveScheduleSettings(scheduleSettings)
        alarmService.cancelAll()
        scheduleSettings.selectedTime.forEach(scheduleRomanTime::invoke)
    }
}