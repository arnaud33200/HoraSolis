package ca.arnaud.horasolis.ui.locationmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.domain.usecase.location.ObserveAllLocationsUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LocationManagerViewModel(
    private val observeAllLocations: ObserveAllLocationsUseCase,
    private val observeLocation: ObserveCurrentLocationUseCase,
    private val setCurrentLocation: SetCurrentLocationUseCase,
    private val screenModelFactory: LocationManagerScreenModelFactory,
) : ViewModel() {

    private val _state = MutableStateFlow<LocationManagerScreenModel>(LocationManagerScreenModel.Empty)
    val state: StateFlow<LocationManagerScreenModel> = _state

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

    fun onSelectLocation(item: LocationItemModel) {
        viewModelScope.launch {
            setCurrentLocation(item.id)
        }
    }
}
