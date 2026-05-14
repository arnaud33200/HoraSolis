package ca.arnaud.horasolis.ui.editalarm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditAlarmDestination(
    alarmId: Long?,
    onBack: () -> Unit,
) {
    val viewModel: EditAlarmViewModel = koinViewModel(
        parameters = { parametersOf(alarmId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditAlarmScreen(
        model = state,
        onBackClick = onBack,
    )
}
