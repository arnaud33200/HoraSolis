package ca.arnaud.horasolis.ui.editalarm

import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel

sealed interface SolisTimeAction: EditAlarmUiAction

sealed interface EditAlarmUiAction {

    data class HourChanged(val hour: Int) : SolisTimeAction

    data class MinuteChanged(val minute: Int) : SolisTimeAction

    data class DayNightToggled(val isDay: Boolean) : SolisTimeAction

    data class LabelChanged(val label: String) : EditAlarmUiAction

    data class DayOfWeekClicked(val item: EditDayOfWeekItemModel) : EditAlarmUiAction

    data object SaveClicked : EditAlarmUiAction
}
