package ca.arnaud.horasolis.domain.provider

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TimeProvider {

    fun getNowDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun getNowDate(): LocalDate {
        return LocalDate.now()
    }

    fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }
}