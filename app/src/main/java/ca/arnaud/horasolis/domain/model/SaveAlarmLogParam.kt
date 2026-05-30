package ca.arnaud.horasolis.domain.model

import java.time.LocalDateTime

sealed interface SaveAlarmLogParam {

    val alarmId: Int

    data class Scheduled(
        override val alarmId: Int,
        val scheduledDateTime: LocalDateTime,
    ) : SaveAlarmLogParam

    data class Cancelled(
        override val alarmId: Int,
    ) : SaveAlarmLogParam

    data class Ringing(
        override val alarmId: Int,
    ) : SaveAlarmLogParam
}
