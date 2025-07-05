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
import androidx.compose.ui.platform.LocalContext
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.extension.getCurrentLocation
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import ca.arnaud.horasolis.ui.timelist.TimeListDestination
import ca.arnaud.horasolis.ui.timelist.TimeListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    val viewModel: TimeListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoraSolisTheme {
                NotificationPermissionRequest()
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    // TEST location
                    val response = context.getCurrentLocation()
                }

                TimeListDestination(
                    viewModel = viewModel,
                )
            }
        }
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

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun LocationPermissionRequest() {
        val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!permissionState.status.isGranted) {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
            if (permissionState.status.shouldShowRationale) {
                Toast.makeText(
                    this,
                    getString(R.string.location_permission_required_toast_message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}