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
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerDestination
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerViewModel
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    //    val viewModel: TimeListViewModel by viewModel()
    val viewModel: AlarmManagerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoraSolisTheme {
                NotificationPermissionRequest()

//                TimeListDestination(
//                    viewModel = viewModel,
//                )
                AlarmManagerDestination(
                    viewModel = viewModel,
                )
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