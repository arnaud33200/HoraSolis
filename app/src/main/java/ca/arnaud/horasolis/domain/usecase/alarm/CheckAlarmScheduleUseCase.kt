package ca.arnaud.horasolis.domain.usecase.alarm

import android.util.Log
import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.data.ScheduleRepository
import ca.arnaud.horasolis.domain.model.alarm.Alarm
import ca.arnaud.horasolis.domain.model.alarm.SavedAlarm
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase

/**
 * Verifies that every saved alarm has a consistent system schedule.
 *
 * For each alarm:
 * - Disabled or repeating with no week days → cancel any existing schedule.
 * - Active with an existing schedule → nothing to do.
 * - Active with no schedule registered → cancel first (safety), then reschedule.
 */
class CheckAlarmScheduleUseCase(
    private val alarmRepository: AlarmRepository,
    private val scheduleRepository: ScheduleRepository,
    private val scheduleNextAlarm: ScheduleNextAlarmUseCase,
    private val timeProvider: TimeProvider,
    private val getSolisDay: GetSolisDayUseCase,
) {

    suspend operator fun invoke() {
        alarmRepository.getAllAlarms().forEach { alarm ->
            check(alarm)
        }
    }

    private suspend fun check(alarm: SavedAlarm) {
        if (!alarm.enabled || alarm.isExpired()) {
            scheduleRepository.cancelAlarm(alarm.id)
            return
        }

        val alreadyScheduled = scheduleRepository.getScheduledAlarmOrNull(alarm.id) != null
        if (!alreadyScheduled) {
            Log.w("CheckAlarmSchedule", "Alarm ${alarm.id} (${alarm.label}) had no schedule — rescheduling")
            scheduleRepository.cancelAlarm(alarm.id)
            scheduleNextAlarm(alarm)
        }
    }

    /**
     * Returns true if the alarm has no remaining occurrences to schedule.
     * - [Alarm.Schedule.Repeating]: no week days selected.
     * - [Alarm.Schedule.OneTime]: the date+civil time is in the past.
     *   If the solis day cannot be resolved, returns false (conservative — let
     *   [ScheduleNextAlarmUseCase] handle the exact expiry check).
     */
    private suspend fun SavedAlarm.isExpired(): Boolean {
        return when (val schedule = this.schedule) {
            is Alarm.Schedule.Repeating -> schedule.weekDays.isEmpty()
            is Alarm.Schedule.OneTime -> {
                val solisDay = getSolisDay(schedule.date).getDataOrNull() ?: return false
                val civilTime = solisTime.toCivilTime(solisDay)
                schedule.date.atTime(civilTime).isBefore(timeProvider.getNowDateTime())
            }
        }
    }
}
