package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.service.RomanTimeAlarmService

class DeleteAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val alarmService: RomanTimeAlarmService,
) {

    suspend operator fun invoke(alarmId: Int) {
        alarmRepository.deleteAlarm(alarmId)
        alarmService.cancelAlarm(alarmId)
    }
}
