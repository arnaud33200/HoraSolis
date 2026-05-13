package ca.arnaud.horasolis.domain.model.alarm

import ca.arnaud.horasolis.domain.model.SolisTime

sealed interface Alarm {

    val label: String?
    val solisTime: SolisTime
    val enabled: Boolean

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
) : Alarm

data class SavedAlarm(
    val id: Int,
    override val label: String?,
    override val solisTime: SolisTime,
    override val enabled: Boolean,
) : Alarm