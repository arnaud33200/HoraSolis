package ca.arnaud.horasolis.domain.model.alarm

import ca.arnaud.horasolis.domain.model.SolisTime
import io.ktor.util.date.WeekDay

/**
 * Alarms the user create and edit from the alarm manager screen
 * When [enabled], it will automatically schedule in [ScheduleSolisAlarmUseCase]
 *
 * @property label The label of the alarm, can be null.
 *  Used to personalize the alarm and show it when alarm is ringing.
 * @property solisTime The time of the alarm, in SolisTime format.
 *  Used to determine when the alarm should ring, and to show the time in the alarm manager screen.
 * @property enabled Whether the alarm is enabled or not.
 *  When enabled, it will automatically schedule
 * @property onForWeekDays The days of the week the alarm should ring on.
 *  When empty, the alarm would not ring on any day, must at least have one day to ring.
 *  By default, the alarm is set to ring on all days of the week (every day).
 */
sealed interface Alarm {

    val label: String?
    val solisTime: SolisTime
    val enabled: Boolean

    val onForWeekDays: Set<WeekDay>

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
}

data class NewAlarm(
    override val label: String?,
    override val solisTime: SolisTime,
    override val enabled: Boolean,
    override val onForWeekDays: Set<WeekDay>,
) : Alarm

data class SavedAlarm(
    val id: Int,
    override val label: String?,
    override val solisTime: SolisTime,
    override val enabled: Boolean,
    override val onForWeekDays: Set<WeekDay>,
) : Alarm