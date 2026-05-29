package ca.arnaud.horasolis.domain.usecase.alarm

import ca.arnaud.horasolis.data.ScheduleRepository

class RefreshAllAlarmScheduleUseCase(
    private val checkAlarmSchedule: CheckAlarmScheduleUseCase,
    private val scheduleRepository: ScheduleRepository,
) {

    suspend operator fun invoke() {
        scheduleRepository.cancelAll()
        checkAlarmSchedule()
    }
}
