package ca.arnaud.horasolis.domain.provider

import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.toSolisTime
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

    fun getNowSolisTime(solisDay: SolisDay): SolisTime {
        return getNowDateTime().toLocalTime().toSolisTime(solisDay)
    }

    fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }
}