package ca.arnaud.horasolis.ui.alarmmanager

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.clock.SolisClockViewModel
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlarmManagerDestination(
    onNavigateToEditAlarm: (alarmId: Int?) -> Unit,
    onNavigateToLocationManager: () -> Unit,
    onNavigateToSolisViewer: () -> Unit,
) {
    NotificationPermissionRequest()

    val viewModel: AlarmManagerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val clockViewModel = koinViewModel<SolisClockViewModel>()
    val clockModel by clockViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is AlarmManagerViewModelEvent.NavigateToEditAlarm -> onNavigateToEditAlarm(event.alarmId)
            }
        }
    }

    AlarmManagerScreen(
        model = state,
        onSnackbarDismissed = {},
        onSolisViewerClick = onNavigateToSolisViewer,
        onLocationClick = onNavigateToLocationManager,
        onAlarmDeleteClick = viewModel::onAlarmDeleteClick,
        onAddClick = viewModel::onAddClick,
        onAlarmItemClick = viewModel::onAlarmItemClick,
        onAlarmToggleClick = viewModel::onAlarmToggleClick,
        clockModel = clockModel,
    )
}

@Composable
fun NotificationPermissionRequest() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    if (!permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        val context = LocalContext.current
        if (permissionState.status.shouldShowRationale) {
            Toast.makeText(
                context,
                stringResource(R.string.notification_permission_required_toast_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
