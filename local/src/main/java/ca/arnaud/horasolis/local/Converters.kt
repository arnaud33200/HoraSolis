package ca.arnaud.horasolis.local

import androidx.room.TypeConverter
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.SolisTime.Type
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

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

    @TypeConverter
    fun fromDayOfWeekSet(value: Set<DayOfWeek>?): String? =
        value?.joinToString(",") { it.name }

    @TypeConverter
    fun toDayOfWeekSet(value: String?): Set<DayOfWeek>? =
        value?.split(",")?.mapNotNull { runCatching { DayOfWeek.valueOf(it) }.getOrNull() }?.toSet()
}
