package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import ca.arnaud.horasolis.domain.repository.LogRepository
import ca.arnaud.horasolis.domain.repository.ScheduleRepository

class CancelAllAlarmUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val logRepository: LogRepository,
) {

    suspend operator fun invoke() {
        val cancelledIds = scheduleRepository.getAllScheduledAlarms().map { it.alarmId }
        scheduleRepository.cancelAll()
        cancelledIds.forEach { logRepository.saveAlarmLog(SaveAlarmLogParam.Cancelled(alarmId = it)) }
    }
}
