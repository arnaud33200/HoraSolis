package ca.arnaud.horasolis.domain.usecase.schedule

import ca.arnaud.horasolis.data.ScheduleSettingsRepository
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.domain.usecase.ScheduleRomanTimeUseCase
import ca.arnaud.horasolis.service.RomanTimeAlarmService

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