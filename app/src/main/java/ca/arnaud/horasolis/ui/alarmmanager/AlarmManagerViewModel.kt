package ca.arnaud.horasolis.ui.alarmmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.extension.setText
import ca.arnaud.horasolis.service.LocationService
import ca.arnaud.horasolis.ui.common.PermissionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AlarmManagerViewModel(
    private val locationService: LocationService,
) : ViewModel() {

    private val _state = MutableStateFlow(AlarmManagerScreenModel())
    val state: StateFlow<AlarmManagerScreenModel> = _state

    fun onCurrentLocationClick(permissionResult: PermissionResult) {
        when (permissionResult) {
            PermissionResult.Granted -> viewModelScope.launch {
                updateCurrentLocation()
            }

            PermissionResult.Denied,
            PermissionResult.PermanentlyDenied -> {
                // TODO - show toast
            }
        }
    }

    private suspend fun updateCurrentLocation() {
        when (val response = locationService.getCurrentLocation()) {
            is Response.Success -> {
                state.value.latitude.setText(response.data.latitude.toString())
                state.value.longitude.setText(response.data.longitude.toString())
            }

            is Response.Failure -> {
                // TODO - show error
            }
        }
    }
}