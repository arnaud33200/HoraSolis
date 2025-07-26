package ca.arnaud.horasolis.domain.usecase.schedule

import ca.arnaud.horasolis.data.ScheduleSettingsRepository
import ca.arnaud.horasolis.domain.model.ScheduleSettings
import ca.arnaud.horasolis.service.SolisTimeAlarmService

// TODO - to remove as we use alarm instead of time schedule
class SavedTimeScheduleUseCase(
    private val scheduleSettingsRepository: ScheduleSettingsRepository,
    private val alarmService: SolisTimeAlarmService,
) {

    suspend operator fun invoke(scheduleSettings: ScheduleSettings) {
        scheduleSettingsRepository.saveScheduleSettings(scheduleSettings)
        alarmService.cancelAll()
//        scheduleSettings.selectedTime.forEach(scheduleRomanTime::invoke)
    }
}