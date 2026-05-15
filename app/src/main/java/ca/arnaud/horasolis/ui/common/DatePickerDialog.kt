package ca.arnaud.horasolis.ui.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import ca.arnaud.horasolis.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    params: DatePickerModel,
) {
    val minDateMillis = remember(params.minDate) { params.minDate.toUtcMillis() }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = remember(params.initialSelectedDate) {
            params.initialSelectedDate?.toUtcMillis()
        },
        selectableDates = remember(minDateMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= minDateMillis
                override fun isSelectableYear(year: Int) = year >= params.minDate.year
            }
        },
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis ?: return@TextButton
                val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                onDateSelected(date)
                onDismiss()
            }) { Text(stringResource(R.string.ok_button)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun LocalDate.toUtcMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
