package ca.arnaud.horasolis.ui.editalarm

import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel
import kotlinx.collections.immutable.ImmutableList

sealed interface EditAlarmScreenModel {

    data object Loading : EditAlarmScreenModel

    data class Content(
        val label: String,
        val hour: Int,
        val minute: Int,
        val isDay: Boolean,
        val civilTime: String,
        val dayOfWeeks: ImmutableList<EditDayOfWeekItemModel>,
    ) : EditAlarmScreenModel
}
