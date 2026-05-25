package ca.arnaud.horasolis.ui.alarmmanager

sealed interface AlarmManagerUiAction {

    data object SolisViewerClicked : AlarmManagerUiAction

    data object ScheduleViewerClicked : AlarmManagerUiAction

    data object LocationClicked : AlarmManagerUiAction

    data object SettingsClicked : AlarmManagerUiAction

    data object AddClicked : AlarmManagerUiAction

    data object SnackbarDismissed : AlarmManagerUiAction

    data class AlarmDeleteClicked(val item: AlarmItemModel) : AlarmManagerUiAction

    data class AlarmItemClicked(val item: AlarmItemModel) : AlarmManagerUiAction

    data class AlarmToggleClicked(val item: AlarmItemModel, val enabled: Boolean) : AlarmManagerUiAction

    data class LocationSelected(val id: String) : AlarmManagerUiAction
}
