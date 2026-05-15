package ca.arnaud.horasolis.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {

    @Serializable
    data object AlarmManager : AppRoute

    @Serializable
    data class EditAlarm(val alarmId: Int?) : AppRoute

    @Serializable
    data object LocationManager : AppRoute

    @Serializable
    data class EditLocation(val locationId: String?) : AppRoute
}
