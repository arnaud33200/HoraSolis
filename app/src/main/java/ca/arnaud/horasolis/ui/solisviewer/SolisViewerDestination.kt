package ca.arnaud.horasolis.ui.solisviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.drop
import ca.arnaud.horasolis.ui.clock.SolisClockViewModel
import ca.arnaud.horasolis.ui.common.DatePickerModel
import ca.arnaud.horasolis.ui.common.DatePickerDialog
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun SolisViewerDestination(
    onBack: () -> Unit,
) {
    val viewModel: SolisViewerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showDatePicker by viewModel.showDatePicker.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val clockViewModel: SolisClockViewModel = koinViewModel()
    val clockModel by clockViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.selectedDate.drop(1).collect { date ->
            clockViewModel.onDateChanged(date)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = viewModel::onDateSelected,
            onDismiss = viewModel::onDismissDatePicker,
            model = DatePickerModel(
                minDate = LocalDate.of(2000, 1, 1),
                initialSelectedDate = selectedDate,
            ),
        )
    }

    SolisViewerScreen(
        model = state,
        clockModel = clockModel,
        onBackClick = onBack,
        onAction = viewModel::onAction,
        onLocationSelected = clockViewModel::onLocationSelected,
    )
}
