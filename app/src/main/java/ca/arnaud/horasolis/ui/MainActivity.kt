package ca.arnaud.horasolis.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import ca.arnaud.horasolis.service.AlarmRingingService
import ca.arnaud.horasolis.ui.common.HoraAlertDialog
import ca.arnaud.horasolis.ui.main.MainViewModel
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoraSolisTheme {
                AppNavigation()

                val ringingDialog by mainViewModel.ringingDialog.collectAsState()
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
}