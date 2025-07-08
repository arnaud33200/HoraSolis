package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository

class DeleteAlarmUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend operator fun invoke(alarmId: Int) {
        alarmRepository.deleteAlarm(alarmId)
    }
}
