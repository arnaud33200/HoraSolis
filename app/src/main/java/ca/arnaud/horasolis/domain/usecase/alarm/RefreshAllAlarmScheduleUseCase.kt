package ca.arnaud.horasolis.domain.usecase.alarm

class RefreshAllAlarmScheduleUseCase(
    private val checkAlarmSchedule: CheckAlarmScheduleUseCase,
    private val cancelAllAlarm: CancelAllAlarmUseCase,
) {

    suspend operator fun invoke() {
        cancelAllAlarm()
        checkAlarmSchedule()
    }
}
