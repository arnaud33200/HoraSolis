package ca.arnaud.horasolis.data.model

import java.time.LocalDateTime

data class ScheduledAlarm(
    val alarmId: Int,
    val scheduledDateTime: LocalDateTime,
)
