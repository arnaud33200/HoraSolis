package ca.arnaud.horasolis.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "solis_day")
data class SolisDayEntity(
    @PrimaryKey val cacheKey: String,
    val civilSunriseTime: LocalTime,
    val civilSunsetTime: LocalTime,
)
