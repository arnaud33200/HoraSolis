package ca.arnaud.horasolis.local

import androidx.room.TypeConverter
import java.time.LocalTime

class Converters {

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }
}

