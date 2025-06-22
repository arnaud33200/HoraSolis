package ca.arnaud.horasolis

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoraSolisTheme {
                NotificationPermissionRequest()
                val state by viewModel.state.collectAsState()
                val showRingingDialog by viewModel.ringingDialog.collectAsState()

                MainScreen(
                    model = state,
                    onCitySelected = viewModel::onCitySelected,
                    onTimeChecked = viewModel::onTimeChecked,
                    onSaveClicked = viewModel::onSaveClicked,
                )

                if (showRingingDialog) {
                    RingtoneDialog(
                        onButtonClick = {
                            if (!AlarmRingingService.stopService(context = this)) {
                                viewModel.onStopRingingServiceFailed()
                            }
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun RingtoneDialog(
        modifier: Modifier = Modifier,
        onButtonClick: () -> Unit,
    ) {
        AlertDialog(
            onDismissRequest = {}, // Not dismissable by outside touch or back press
            confirmButton = {
                Button(onClick = onButtonClick) {
                    Text(stringResource(id = R.string.ringing_alarm_dialog_button))
                }
            },
            title = {
                Text(stringResource(id = R.string.ringing_alarm_dialog_title))
            },
            text = {
                Text(stringResource(id = R.string.ringing_alarm_dialog_message))
            },
            modifier = modifier
        )
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun NotificationPermissionRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        if (!permissionState.status.isGranted) {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
            if (permissionState.status.shouldShowRationale) {
                Toast.makeText(
                    this,
                    getString(R.string.notification_permission_required_toast_message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
