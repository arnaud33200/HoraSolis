package ca.arnaud.horasolis.ui.locationmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.usecase.location.DeleteLocationError
import ca.arnaud.horasolis.domain.usecase.location.DeleteLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveAllLocationsUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface LocationManagerViewModelEvent {
    data class NavigateToEditLocation(val locationId: String?) : LocationManagerViewModelEvent
}

sealed interface LocationManagerDialog {
    data class ConfirmDelete(val item: LocationItemModel) : LocationManagerDialog
    data object LastLocationError : LocationManagerDialog
}

class LocationManagerViewModel(
    private val observeAllLocations: ObserveAllLocationsUseCase,
    private val observeLocation: ObserveCurrentLocationUseCase,
    private val setCurrentLocation: SetCurrentLocationUseCase,
    private val deleteLocation: DeleteLocationUseCase,
    private val screenModelFactory: LocationManagerScreenModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow<LocationManagerScreenModel>(LocationManagerScreenModel.Empty)
    val state: StateFlow<LocationManagerScreenModel> = _state

    private val _event = MutableSharedFlow<LocationManagerViewModelEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<LocationManagerViewModelEvent> = _event

    private val _dialogState = MutableStateFlow<LocationManagerDialog?>(null)
    val dialogState: StateFlow<LocationManagerDialog?> = _dialogState

    init {
        viewModelScope.launch {
            combine(
                observeAllLocations(),
                observeLocation(),
            ) { all, current ->
                screenModelFactory.create(allLocations = all, currentLocation = current)
            }.collectLatest { model ->
                _state.value = model
            }
        }
    }

    fun onAction(action: LocationManagerUserAction) {
        viewModelScope.launch {
            when (action) {
                is LocationManagerUserAction.RowClick -> setCurrentLocation(action.item.id)
                is LocationManagerUserAction.EditClick -> _event.emit(
                    LocationManagerViewModelEvent.NavigateToEditLocation(action.item.id)
                )
                is LocationManagerUserAction.DeleteClick -> _dialogState.value =
                    LocationManagerDialog.ConfirmDelete(action.item)
                LocationManagerUserAction.AddClick -> _event.emit(
                    LocationManagerViewModelEvent.NavigateToEditLocation(null)
                )
            }
        }
    }

    fun onConfirmDelete() {
        val item = (_dialogState.value as? LocationManagerDialog.ConfirmDelete)?.item ?: return
        viewModelScope.launch {
            when (val result = deleteLocation(item.id)) {
                is Response.Success -> onDismissDialog()
                is Response.Failure -> {
                    _dialogState.value = when (result.error) {
                        DeleteLocationError.LastLocation -> LocationManagerDialog.LastLocationError
                    }
                }
            }
        }
    }

    fun onDismissDialog() {
        _dialogState.value = null
    }
}
