package ca.arnaud.horasolis.domain.provider

import java.time.LocalDateTime

interface AlarmScheduler {
    fun scheduleAlarm(alarmId: Int, dateTime: LocalDateTime)
    fun cancelAlarm(alarmId: Int)
    fun cancelAll()
}
