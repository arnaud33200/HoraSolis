package ca.arnaud.horasolis.ui.editlocation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.model.common.UpdateParam
import ca.arnaud.horasolis.domain.model.location.LocationUpdateParams
import ca.arnaud.horasolis.domain.model.location.LocationValidator
import ca.arnaud.horasolis.domain.usecase.location.GetLocationOrNullUseCase
import ca.arnaud.horasolis.domain.usecase.location.SaveLocationParams
import ca.arnaud.horasolis.domain.usecase.location.SaveLocationUseCase
import ca.arnaud.horasolis.extension.PermissionResult
import ca.arnaud.horasolis.extension.setText
import ca.arnaud.horasolis.service.LocationService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditLocationFieldStates(
    val name: TextFieldState = TextFieldState(),
    val latitude: TextFieldState = TextFieldState(),
    val longitude: TextFieldState = TextFieldState(),
)

sealed interface EditLocationViewModelParams {
    data object New : EditLocationViewModelParams
    data class Edit(val locationId: String) : EditLocationViewModelParams
}

sealed interface EditLocationEvent {
    data object SaveSuccess : EditLocationEvent
}

class EditLocationViewModel(
    private val params: EditLocationViewModelParams,
    private val locationService: LocationService,
    private val saveLocation: SaveLocationUseCase,
    private val getLocationOrNull: GetLocationOrNullUseCase,
    private val locationValidator: LocationValidator,
) : ViewModel() {

    private val _state = MutableStateFlow(EditLocationScreenModel())
    val state: StateFlow<EditLocationScreenModel> = _state

    private val _event = MutableSharedFlow<EditLocationEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<EditLocationEvent> = _event

    private var locationUpdateParams = LocationUpdateParams()
    private var initialLocation: SavedLocation = SavedLocation.empty

    init {
        viewModelScope.launch {
            if (params is EditLocationViewModelParams.Edit) {
                getLocationOrNull(params.locationId)?.let { location ->
                    initialLocation = location
                    state.value.fieldStates.name.setText(location.name)
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

    fun onSaveClick() {
        viewModelScope.launch {
            val saveParams = when (params) {
                is EditLocationViewModelParams.New -> SaveLocationParams.New(locationUpdateParams)
                is EditLocationViewModelParams.Edit -> SaveLocationParams.Edit(params.locationId, locationUpdateParams)
            }
            saveLocation(saveParams)
            _event.emit(EditLocationEvent.SaveSuccess)
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
        combine(
            snapshotFlow { state.value.fieldStates.name.text },
            snapshotFlow { state.value.fieldStates.latitude.text },
            snapshotFlow { state.value.fieldStates.longitude.text },
        ) { name, latitude, longitude ->
            LocationUpdateParams(
                name = UpdateParam.updateIfNotNull(name.toString().takeIf { it.isNotBlank() }),
                lat = UpdateParam.updateIfNotNull(latitude.toString().toDoubleOrNull()),
                lng = UpdateParam.updateIfNotNull(longitude.toString().toDoubleOrNull()),
            )
        }.collectLatest { updateParams ->
            locationUpdateParams = updateParams
            _state.update { model ->
                model.copy(saveEnabled = locationValidator.isValid(updateParams, initialLocation))
            }
        }
    }
}
