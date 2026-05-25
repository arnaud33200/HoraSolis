package ca.arnaud.horasolis.ui.editalarm

import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel
import kotlinx.collections.immutable.ImmutableList

sealed interface EditAlarmScreenModel {

    data object Loading : EditAlarmScreenModel

    data class Content(
        val hour: Int,
        val minute: Int,
        val isDay: Boolean,
        val civilTime: String,
        val scheduleContent: ScheduleContent,
        val soundName: String,
        val vibrationEnabled: Boolean = true,
        val saveEnabled: Boolean = false,
    ) : EditAlarmScreenModel
}

sealed interface ScheduleContent {
    data class Repeating(val dayOfWeeks: ImmutableList<EditDayOfWeekItemModel>) : ScheduleContent
    data class OneTime(val selectedDate: String) : ScheduleContent
}
