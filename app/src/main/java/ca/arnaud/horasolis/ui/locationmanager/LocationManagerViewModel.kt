package ca.arnaud.horasolis.ui.locationmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.model.SavedLocation
import ca.arnaud.horasolis.domain.usecase.location.ObserveAllLocationsUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationParams
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LocationManagerViewModel(
    private val observeAllLocations: ObserveAllLocationsUseCase,
    private val observeLocation: ObserveLocationUseCase,
    private val setCurrentLocation: SetCurrentLocationUseCase,
    private val screenModelFactory: LocationManagerScreenModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow<LocationManagerScreenModel>(LocationManagerScreenModel.Empty)
    val state: StateFlow<LocationManagerScreenModel> = _state

    private val _editLocationDialog = MutableStateFlow<EditLocationDialogModel?>(null)
    val editLocationDialog: StateFlow<EditLocationDialogModel?> = _editLocationDialog

    private var currentLocations: List<SavedLocation> = emptyList()

    init {
        viewModelScope.launch {
            combine(
                observeAllLocations(),
                observeLocation(),
            ) { all, current ->
                currentLocations = all
                screenModelFactory.create(allLocations = all, currentLocation = current)
            }.collectLatest { model ->
                _state.value = model
            }
        }
    }

    fun onAddClick() {
        _editLocationDialog.value = EditLocationDialogModel(locationId = null)
    }

    fun onEditLocation(item: LocationItemModel) {
        _editLocationDialog.value = EditLocationDialogModel(locationId = item.id)
    }

    fun onDialogDismiss() {
        _editLocationDialog.value = null
    }

    fun onSelectLocation(item: LocationItemModel) {
        val location = currentLocations.firstOrNull { it.id == item.id } ?: return
        viewModelScope.launch {
            setCurrentLocation(SetCurrentLocationParams(lat = location.lat, long = location.lng))
        }
    }
}
