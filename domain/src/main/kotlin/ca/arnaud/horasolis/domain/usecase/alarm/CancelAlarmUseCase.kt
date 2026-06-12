package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam
import ca.arnaud.horasolis.domain.repository.LogRepository
import ca.arnaud.horasolis.domain.repository.ScheduleRepository

class CancelAlarmUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val logRepository: LogRepository,
) {

    suspend operator fun invoke(alarmId: Int) {
        scheduleRepository.cancelAlarm(alarmId)
        logRepository.saveAlarmLog(SaveAlarmLogParam.Cancelled(alarmId = alarmId))
    }
}
