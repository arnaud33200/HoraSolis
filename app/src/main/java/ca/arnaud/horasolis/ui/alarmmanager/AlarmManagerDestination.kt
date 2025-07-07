package ca.arnaud.horasolis.ui.alarmmanager

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ca.arnaud.horasolis.extension.PermissionResult
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun AlarmManagerDestination(
    viewModel: AlarmManagerViewModel,
) {
    val state by viewModel.state.collectAsState()
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

    AlarmManagerScreen(
        model = state,
        onSnackbarDismissed = {},
        onCurrentLocationClick = {
            currentLocationPermissionState.launchPermissionRequest()
        },
    )
}
