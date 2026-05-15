package ca.arnaud.horasolis.ui.editalarm

import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel
import java.time.LocalDate

sealed interface SolisTimeAction: EditAlarmUiAction

sealed interface EditAlarmUiAction {

    data class HourChanged(val hour: Int) : SolisTimeAction

    data class MinuteChanged(val minute: Int) : SolisTimeAction

    data class DayNightToggled(val isDay: Boolean) : SolisTimeAction

    data class DayOfWeekClicked(val item: EditDayOfWeekItemModel) : EditAlarmUiAction

    data class ScheduleTypeSelected(val isRepeating: Boolean) : EditAlarmUiAction

    data class DateSelected(val date: LocalDate) : EditAlarmUiAction

    data object SaveClicked : EditAlarmUiAction
}
