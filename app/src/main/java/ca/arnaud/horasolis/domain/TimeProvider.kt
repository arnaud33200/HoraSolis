package ca.arnaud.horasolis.domain

import java.time.LocalDate
import java.time.LocalDateTime

class TimeProvider {

    fun getNowDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun getNowDate(): LocalDate {
        return LocalDate.now()
    }
}