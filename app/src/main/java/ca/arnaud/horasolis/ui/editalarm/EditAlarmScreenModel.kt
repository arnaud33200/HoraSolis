package ca.arnaud.horasolis.ui.editalarm

sealed interface EditAlarmScreenModel {

    data object Loading : EditAlarmScreenModel

    data object Content : EditAlarmScreenModel
}
