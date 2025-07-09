package ca.arnaud.horasolis.local

import androidx.room.TypeConverter
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.SolisTime.Type
import java.time.LocalTime

class Converters {

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromSolisTime(value: SolisTime?): String? = value?.let { time ->
        "${time.hour}:${time.minute}:${time.type.name}"
    }

    @TypeConverter
    fun toSolisTime(value: String?): SolisTime? = value?.split(":")?.let {
        if (it.size == 3) {
            SolisTime(
                hour = it[0].toIntOrNull() ?: 0,
                minute = it[1].toIntOrNull() ?: 0,
                type = Type.fromNameOrNull(it[2]) ?: Type.Day,
            )
        } else null
    }
}
