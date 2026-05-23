package ca.arnaud.horasolis.ui.scheduleviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScheduleViewerDestination(
    onBack: () -> Unit,
) {
    val viewModel: ScheduleViewerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScheduleViewerScreen(
        model = state,
        onBackClick = onBack,
    )
}
