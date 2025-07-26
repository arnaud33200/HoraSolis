package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.usecase.location.GetCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationParams
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import ca.arnaud.horasolis.extension.PermissionResult
import ca.arnaud.horasolis.extension.setText
import ca.arnaud.horasolis.service.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditLocationViewModel(
    private val locationService: LocationService,
    private val setCurrentLocation: SetCurrentLocationUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditLocationDialogModel())
    val state: StateFlow<EditLocationDialogModel> = _state

    init {
        viewModelScope.launch {
            getCurrentLocation()?.let { userLocation ->
                state.value.latitude.setText(userLocation.lat.toString())
                state.value.longitude.setText(userLocation.lng.toString())
            }
            observeLocationTextFields()
        }
    }

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

    fun onSaveLocationClick() {
        viewModelScope.launch {
            val latitude = state.value.latitude.text.toString()
            val longitude = state.value.longitude.text.toString()
            val params = SetCurrentLocationParams(
                lat = latitude.toDoubleOrNull() ?: 0.0,
                long = longitude.toDoubleOrNull() ?: 0.0,
            )
            setCurrentLocation(params)
            _state.update { model ->
                model.copy(requestDismiss = true)
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

    private suspend fun observeLocationTextFields() {
        snapshotFlow {
            state.value.latitude.text to state.value.longitude.text
        }.collectLatest { (latitude, longitude) ->
            _state.update { model ->
                model.copy(saveEnabled = latitude.isNotBlank() && longitude.isNotBlank())
            }
        }
    }

    fun onRequestDismissHandled() {
        _state.update { model ->
            model.copy(requestDismiss = false)
        }
    }
}
