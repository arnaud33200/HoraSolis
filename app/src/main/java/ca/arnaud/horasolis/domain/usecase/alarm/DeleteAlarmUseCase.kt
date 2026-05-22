package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.data.ScheduleRepository

class DeleteAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val scheduleRepository: ScheduleRepository,
) {

    suspend operator fun invoke(alarmId: Int) {
        alarmRepository.deleteAlarm(alarmId)
        scheduleRepository.cancelAlarm(alarmId)
    }
}
