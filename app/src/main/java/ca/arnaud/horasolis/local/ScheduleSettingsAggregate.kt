package ca.arnaud.horasolis.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Aggregate data class containing schedule settings and selected times.
 */
data class ScheduleSettingsAggregate(
    @Embedded val settings: ScheduleSettingsEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "scheduleSettingsId"
    )
    val selectedTimes: List<SelectedTimeEntity>,
)
