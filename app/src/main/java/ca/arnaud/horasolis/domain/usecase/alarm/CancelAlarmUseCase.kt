package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.LogRepository
import ca.arnaud.horasolis.data.ScheduleRepository
import ca.arnaud.horasolis.domain.model.SaveAlarmLogParam

class CancelAlarmUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val logRepository: LogRepository,
) {

    suspend operator fun invoke(alarmId: Int) {
        scheduleRepository.cancelAlarm(alarmId)
        logRepository.saveAlarmLog(SaveAlarmLogParam.Cancelled(alarmId = alarmId))
    }
}
