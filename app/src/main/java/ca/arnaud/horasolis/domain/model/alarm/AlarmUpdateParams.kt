package ca.arnaud.horasolis.domain.model.alarm

import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.domain.model.common.UpdateParam
import ca.arnaud.horasolis.domain.model.common.UpdateParam.Unchanged.getUpdateDataOrDefault

data class AlarmUpdateParams(
    val label: UpdateParam<String?> = UpdateParam.Unchanged,
    val solisTime: UpdateParam<SolisTime> = UpdateParam.Unchanged,
    val enabled: UpdateParam<Boolean> = UpdateParam.Unchanged,
    val schedule: UpdateParam<Alarm.Schedule> = UpdateParam.Unchanged,
)

fun Alarm.applyUpdates(
    updateParams: AlarmUpdateParams
): Alarm {
    return when (this) {
        is NewAlarm -> NewAlarm(
            label = updateParams.label.getUpdateDataOrDefault(this.label),
            solisTime = updateParams.solisTime.getUpdateDataOrDefault(this.solisTime),
            enabled = updateParams.enabled.getUpdateDataOrDefault(this.enabled),
            schedule = updateParams.schedule.getUpdateDataOrDefault(this.schedule),
        )

        is SavedAlarm -> SavedAlarm(
            id = this.id,
            label = updateParams.label.getUpdateDataOrDefault(this.label),
            solisTime = updateParams.solisTime.getUpdateDataOrDefault(this.solisTime),
            enabled = updateParams.enabled.getUpdateDataOrDefault(this.enabled),
            schedule = updateParams.schedule.getUpdateDataOrDefault(this.schedule),
        )
    }
}