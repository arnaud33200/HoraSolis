package ca.arnaud.horasolis.ui.onboarding

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.extension.PermissionResult
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingDestination(
    onNavigateToAlarmManager: () -> Unit,
    onNavigateToEditLocation: () -> Unit,
) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = { granted ->
            val result = if (granted) PermissionResult.Granted else PermissionResult.Denied
            viewModel.onCurrentLocationPermissionResult(result)
        },
    )

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                OnboardingViewModelEvent.NavigateToAlarmManager -> onNavigateToAlarmManager()
                OnboardingViewModelEvent.NavigateToEditLocation -> onNavigateToEditLocation()
            }
        }
    }

    OnboardingScreen(
        model = state,
        onUserAction = { action ->
            when (action) {
                OnboardingUserAction.CurrentLocation -> locationPermissionState.launchPermissionRequest()
                else -> viewModel.onUserAction(action)
            }
        },
    )
}
