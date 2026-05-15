package ca.arnaud.horasolis.ui.editalarm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.arnaud.horasolis.ui.common.DatePickerDialog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditAlarmDestination(
    alarmId: Int?,
    onBack: () -> Unit,
) {
    val viewModel: EditAlarmViewModel = koinViewModel(
        parameters = {
            val viewModelParams = if (alarmId != null) {
                EditAlarmViewModelParams.Edit(alarmId)
            } else {
                EditAlarmViewModelParams.New
            }
            parametersOf(viewModelParams)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val datePickerParams by viewModel.datePickerModel.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                EditAlarmViewModelEvent.SaveSuccess -> onBack()
            }
        }
    }

    datePickerParams?.let { params ->
        DatePickerDialog(
            params = params,
            onDateSelected = { date -> viewModel.onAction(EditAlarmUiAction.DateSelected(date)) },
            onDismiss = { viewModel.onAction(EditAlarmUiAction.DatePickerDismissed) },
        )
    }

    EditAlarmScreen(
        model = state,
        labelState = viewModel.labelState,
        onBackClick = onBack,
        onAction = viewModel::onAction,
    )
}
