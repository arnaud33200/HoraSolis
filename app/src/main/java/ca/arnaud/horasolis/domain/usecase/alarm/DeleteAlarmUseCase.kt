package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository

class DeleteAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val cancelAlarm: CancelAlarmUseCase,
) {

    suspend operator fun invoke(alarmId: Int) {
        alarmRepository.deleteAlarm(alarmId)
        cancelAlarm(alarmId)
    }
}
