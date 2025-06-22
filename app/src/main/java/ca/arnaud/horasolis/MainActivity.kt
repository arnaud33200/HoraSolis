package ca.arnaud.horasolis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HoraSolisTheme {
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
                            AlarmRingingService.stopService(context = this)
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
}
