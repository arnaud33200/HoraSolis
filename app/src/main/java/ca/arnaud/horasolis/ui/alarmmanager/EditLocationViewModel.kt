package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.usecase.location.GetLocationOrNullUseCase
import ca.arnaud.horasolis.domain.model.common.UpdateParam
import ca.arnaud.horasolis.domain.model.location.LocationUpdateParams
import ca.arnaud.horasolis.domain.usecase.location.SaveLocationParams
import ca.arnaud.horasolis.domain.usecase.location.SaveLocationUseCase
import ca.arnaud.horasolis.extension.PermissionResult
import ca.arnaud.horasolis.extension.setText
import ca.arnaud.horasolis.service.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditLocationFieldStates(
    val latitude: TextFieldState = TextFieldState(),
    val longitude: TextFieldState = TextFieldState(),
)

sealed interface EditLocationViewModelParams {
    data object New : EditLocationViewModelParams
    data class Edit(val locationId: String) : EditLocationViewModelParams
}

class EditLocationViewModel(
    private val params: EditLocationViewModelParams,
    private val locationService: LocationService,
    private val saveLocation: SaveLocationUseCase,
    private val getLocation: GetLocationOrNullUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditLocationDialogModel())
    val state: StateFlow<EditLocationDialogModel> = _state

    private var locationUpdateParams = LocationUpdateParams()

    init {
        viewModelScope.launch {
            if (params is EditLocationViewModelParams.Edit) {
                getLocation(params.locationId)?.let { location ->
                    state.value.fieldStates.latitude.setText(location.lat.toString())
                    state.value.fieldStates.longitude.setText(location.lng.toString())
                }
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
            val saveParams = when (params) {
                is EditLocationViewModelParams.New -> SaveLocationParams.New(locationUpdateParams)
                is EditLocationViewModelParams.Edit -> SaveLocationParams.Edit(params.locationId, locationUpdateParams)
            }
            saveLocation(saveParams)
            _state.update { model ->
                model.copy(requestDismiss = true)
            }
        }
    }

    private suspend fun updateCurrentLocation() {
        when (val response = locationService.getCurrentLocation()) {
            is Response.Success -> {
                state.value.fieldStates.latitude.setText(response.data.latitude.toString())
                state.value.fieldStates.longitude.setText(response.data.longitude.toString())
            }

            is Response.Failure -> {
                // TODO - show error
            }
        }
    }

    private suspend fun observeLocationTextFields() {
        snapshotFlow {
            state.value.fieldStates.latitude.text to state.value.fieldStates.longitude.text
        }.collectLatest { (latitude, longitude) ->
            locationUpdateParams = LocationUpdateParams(
                lat = UpdateParam.updateIfNotNull(latitude.toString().toDoubleOrNull()),
                lng = UpdateParam.updateIfNotNull(longitude.toString().toDoubleOrNull()),
            )
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
