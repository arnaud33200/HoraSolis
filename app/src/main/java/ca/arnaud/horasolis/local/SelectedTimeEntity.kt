package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a selected Roman time for Room database.
 *
 * @property id Auto-generated primary key.
 * @property number The number of the time in the day or night (1 to 24).
 * @property startTime The start time of the period, stored as an ISO-8601 string (e.g., "13:45:00").
 * @property duration The duration of the period in seconds.
 * @property type The type of the period, either "Day" or "Night".
 */
@Entity(tableName = "selected_times")
data class SelectedTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: Int,
    val startTime: String,
    val duration: Long,
    val type: String
)
