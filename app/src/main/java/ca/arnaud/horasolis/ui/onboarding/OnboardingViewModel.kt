package ca.arnaud.horasolis.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.Response
import ca.arnaud.horasolis.domain.model.SolisDay
import ca.arnaud.horasolis.domain.model.common.UpdateParam
import ca.arnaud.horasolis.domain.model.location.LocationUpdateParams
import ca.arnaud.horasolis.domain.onFailure
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisDayError
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SaveLocationParams
import ca.arnaud.horasolis.domain.usecase.location.SaveLocationUseCase
import ca.arnaud.horasolis.extension.PermissionResult
import ca.arnaud.horasolis.service.LocationService
import ca.arnaud.horasolis.ui.common.StringProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface OnboardingViewModelEvent {
    data object NavigateToAlarmManager : OnboardingViewModelEvent
    data object NavigateToEditLocation : OnboardingViewModelEvent
}

class OnboardingViewModel(
    private val getSolisDay: GetSolisDayUseCase,
    private val timeProvider: TimeProvider,
    private val stringProvider: StringProvider,
    private val observeCurrentLocation: ObserveCurrentLocationUseCase,
    private val locationService: LocationService,
    private val saveLocation: SaveLocationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<OnboardingScreenModel>(OnboardingScreenModel.Loading)
    val state: StateFlow<OnboardingScreenModel> = _state

    private val _event = MutableSharedFlow<OnboardingViewModelEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<OnboardingViewModelEvent> = _event

    init {
        viewModelScope.launch {
            checkOnboarding().onFailure {
                observeCurrentLocation().collect { location ->
                    if (location != null) {
                        checkOnboarding()
                    }
                }
            }
        }
    }

    fun onUserAction(action: OnboardingUserAction) {
        viewModelScope.launch {
            when (action) {
                OnboardingUserAction.Retry -> {
                    _state.value = OnboardingScreenModel.Loading
                    checkOnboarding()
                }

                OnboardingUserAction.AddLocation -> _event.emit(
                    OnboardingViewModelEvent.NavigateToEditLocation
                )

                OnboardingUserAction.CurrentLocation -> {
                    // No-op, handle in the destination
                }
            }
        }
    }

    fun onCurrentLocationPermissionResult(result: PermissionResult) {
        viewModelScope.launch {
            when (result) {
                PermissionResult.Granted -> addNewLocation()

                PermissionResult.Denied,
                PermissionResult.PermanentlyDenied -> _event.emit(
                    OnboardingViewModelEvent.NavigateToEditLocation
                )
            }
        }
    }

    private suspend fun addNewLocation() {
        _state.value = OnboardingScreenModel.MissingLocation(loading = true)
        val locationResponse = locationService.getCurrentLocation()
        when (locationResponse) {
            is Response.Success -> {
                val params = LocationUpdateParams(
                    name = UpdateParam.Update(
                        stringProvider.getString(R.string.default_current_location_name)
                    ),
                    lat = UpdateParam.Update(locationResponse.data.latitude),
                    lng = UpdateParam.Update(locationResponse.data.longitude),
                )
                saveLocation(SaveLocationParams.New(params))
                OnboardingScreenModel.MissingLocation(loading = false)
            }

            is Response.Failure -> {
                OnboardingScreenModel.MissingLocation(loading = false)
                _event.emit(OnboardingViewModelEvent.NavigateToEditLocation)
            }
        }
    }

    private suspend fun checkOnboarding(): Response<SolisDay, GetSolisDayError> {
        val response = getSolisDay(timeProvider.getNowDate())
        _state.value = when (response) {
            is Response.Success -> OnboardingScreenModel.Ready
            is Response.Failure -> when (response.error) {
                GetSolisDayError.NoLocation -> OnboardingScreenModel.MissingLocation()
                GetSolisDayError.Unknown -> OnboardingScreenModel.Error
            }
        }
        if (response is Response.Success) {
            _event.emit(OnboardingViewModelEvent.NavigateToAlarmManager)
        }
        return response
    }
}
