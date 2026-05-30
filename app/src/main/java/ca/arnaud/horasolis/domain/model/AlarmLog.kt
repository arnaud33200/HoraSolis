package ca.arnaud.horasolis.domain.model

import java.time.LocalDateTime

sealed interface AlarmLog {

    val id: Long
    val alarmId: Int
    val timestamp: LocalDateTime

    data class Scheduled(
        override val id: Long = 0,
        override val alarmId: Int,
        override val timestamp: LocalDateTime = LocalDateTime.MIN,
        val scheduledDateTime: LocalDateTime,
    ) : AlarmLog

    data class Cancelled(
        override val id: Long = 0,
        override val alarmId: Int,
        override val timestamp: LocalDateTime = LocalDateTime.MIN,
    ) : AlarmLog

    data class Ringing(
        override val id: Long = 0,
        override val alarmId: Int,
        override val timestamp: LocalDateTime = LocalDateTime.MIN,
    ) : AlarmLog
}
