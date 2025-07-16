package ca.arnaud.horasolis.domain.usecase

import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.provider.TimeProvider

data class ScheduleNextDayAlarmParam(
    val number: Int,
)

class ScheduleNextDayAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val scheduleAlarm: ScheduleSolisAlarmUseCase,
    private val timeProvider: TimeProvider,
) {

    // TODO - return a Response (#8)
    suspend operator fun invoke(param: ScheduleNextDayAlarmParam) {
        val date = timeProvider.getNowDate().plusDays(1)
        val alarm = alarmRepository.getAlarm(param.number) ?: return
        scheduleAlarm(alarm, date)

    }
}