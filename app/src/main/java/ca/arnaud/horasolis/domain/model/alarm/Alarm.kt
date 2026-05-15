package ca.arnaud.horasolis.domain.model.alarm

import ca.arnaud.horasolis.domain.model.SolisTime
import io.ktor.util.date.WeekDay
import java.time.LocalDate

/**
 * Alarms the user create and edit from the alarm manager screen
 * When [enabled], it will automatically schedule in [ca.arnaud.horasolis.domain.usecase.alarm.ScheduleNextAlarmUseCase]
 *
 * @property label The label of the alarm, can be null.
 *  Used to personalize the alarm and show it when alarm is ringing.
 * @property solisTime The time of the alarm, in SolisTime format.
 *  Used to determine when the alarm should ring, and to show the time in the alarm manager screen.
 * @property enabled Whether the alarm is enabled or not.
 *  When enabled, it will automatically schedule
 * @property schedule The schedule of the alarm, either a one-time date or repeating week days.
 *  When [Alarm.Schedule.Repeating] with empty week days, the alarm would not ring on any day.
 */
sealed interface Alarm {

    val label: String?
    val solisTime: SolisTime
    val enabled: Boolean
    val schedule: Schedule

    sealed interface Schedule {

        data class OneTime(val date: LocalDate): Schedule
        data class Repeating(val weekDays: Set<WeekDay>): Schedule
    }

    /**
     * Compares two alarms for ordering, used to sort alarms in a list.
     * sort by type (day before night), then by hour, then by minute.
     *
     * @param other The other alarm to compare to.
     * @return A negative integer, zero, or a positive integer as this alarm
     */
    fun compareTo(other: SavedAlarm): Int {
        val typeComparison = this.solisTime.type.compareTo(other.solisTime.type)
        if (typeComparison != 0) {
            return typeComparison
        }

        val hourComparison = this.solisTime.hour.compareTo(other.solisTime.hour)
        if (hourComparison != 0) {
            return hourComparison
        }

        return this.solisTime.minute.compareTo(other.solisTime.minute)
    }

    fun copy(
        solisTime: SolisTime = this.solisTime,
        schedule: Schedule = this.schedule,
    ): Alarm {
        return when (this) {
            is NewAlarm -> NewAlarm(
                label = this.label,
                solisTime = solisTime,
                enabled = this.enabled,
                schedule = schedule,
            )

            is SavedAlarm -> SavedAlarm(
                id = this.id,
                label = this.label,
                solisTime = solisTime,
                enabled = this.enabled,
                schedule = schedule,
            )
        }
    }

    companion object {

        val empty: Alarm = NewAlarm(
            label = null,
            solisTime = SolisTime(0, 0, 0, SolisTime.Type.Day),
            enabled = false,
            schedule = Schedule.Repeating(emptySet()),
        )
    }
}

data class NewAlarm(
    override val label: String?,
    override val solisTime: SolisTime,
    override val enabled: Boolean,
    override val schedule: Alarm.Schedule,
) : Alarm

data class SavedAlarm(
    val id: Int,
    override val label: String?,
    override val solisTime: SolisTime,
    override val enabled: Boolean,
    override val schedule: Alarm.Schedule,
) : Alarm