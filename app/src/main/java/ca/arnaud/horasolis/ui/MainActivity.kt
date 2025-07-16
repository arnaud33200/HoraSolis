package ca.arnaud.horasolis.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.service.AlarmRingingService
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerDestination
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerViewModel
import ca.arnaud.horasolis.ui.common.HoraAlertDialog
import ca.arnaud.horasolis.ui.main.MainViewModel
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    //    val viewModel: TimeListViewModel by viewModel()
    val mainViewModel: MainViewModel by viewModel()
    val alarmManagerViewModel: AlarmManagerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoraSolisTheme {
                NotificationPermissionRequest()

                val ringingDialog by mainViewModel.ringingDialog.collectAsState()

                AlarmManagerDestination(
                    viewModel = alarmManagerViewModel,
                )

                ringingDialog?.let { dialogModel ->
                    val context = LocalContext.current
                    HoraAlertDialog(
                        model = dialogModel,
                        onButtonClick = {
                            if (!AlarmRingingService.stopService(context = context)) {
                                mainViewModel.onStopRingingServiceFailed()
                            }
                        },
                    )
                }
            }
        }
    }


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