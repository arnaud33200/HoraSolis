package ca.arnaud.horasolis.domain.model

import java.time.LocalDateTime

data class ScheduledAlarm(
    val alarmId: Int,
    val scheduledDateTime: LocalDateTime,
)
