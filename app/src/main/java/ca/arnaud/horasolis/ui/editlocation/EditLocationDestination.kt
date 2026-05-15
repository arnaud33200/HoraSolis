package ca.arnaud.horasolis.ui.editlocation

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.extension.PermissionResult
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditLocationDestination(
    locationId: String?,
    onBack: () -> Unit,
) {
    val viewModel: EditLocationViewModel = koinViewModel(
        parameters = {
            val params = if (locationId == null) {
                EditLocationViewModelParams.New
            } else {
                EditLocationViewModelParams.Edit(locationId)
            }
            parametersOf(params)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    val currentLocationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = { granted ->
            val permissionResult = when (granted) {
                true -> PermissionResult.Granted
                false -> PermissionResult.Denied
            }
            viewModel.onCurrentLocationClick(permissionResult)
        }
    )

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                EditLocationEvent.SaveSuccess -> onBack()
            }
        }
    }

    EditLocationScreen(
        model = state,
        onBack = onBack,
        onSaveClick = viewModel::onSaveClick,
        onCurrentLocationClick = currentLocationPermissionState::launchPermissionRequest,
    )
}
