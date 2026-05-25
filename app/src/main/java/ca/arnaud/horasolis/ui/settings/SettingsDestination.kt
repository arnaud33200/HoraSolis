package ca.arnaud.horasolis.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.ui.editalarm.rememberRingtonePickerLauncher
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsDestination(
    onBack: () -> Unit,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val ringtoneLauncher = rememberRingtonePickerLauncher { result ->
        viewModel.onAction(SettingsUiAction.SoundResult(result))
    }

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                SettingsViewModelEvent.SaveSuccess -> onBack()
                is SettingsViewModelEvent.LaunchRingtonePicker -> {
                    ringtoneLauncher.launch(event.currentUri)
                }
            }
        }
    }

    SettingsScreen(
        onBackClick = onBack,
        onAction = viewModel::onAction,
        model = state,
    )
}
