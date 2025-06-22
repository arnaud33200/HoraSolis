package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing the schedule settings for alarms.
 *
 * @property id Auto-generated primary key (should always be 1, singleton row).
 * @property lat Latitude for the schedule.
 * @property lng Longitude for the schedule.
 * @property timZoneId Time zone ID for the schedule.
 */
@Entity(tableName = "schedule_settings")
data class ScheduleSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val lat: Double,
    val lng: Double,
    val timZoneId: String
)
