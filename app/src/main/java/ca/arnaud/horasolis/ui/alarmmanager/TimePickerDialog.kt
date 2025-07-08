package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalTime

data class TimePickerDialogModel(
    val hour: Int = 12,
    val minute: Int = 0,
)

@Composable
fun TimePickerDialog(
    model: TimePickerDialogModel = TimePickerDialogModel(),
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timeState = rememberTimePickerState(
        initialHour = model.hour,
        initialMinute = model.minute
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Column {
                TimePicker(state = timeState)
                Text(
                    text = "Selected time: ${timeState.hour}:${timeState.minute}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(LocalTime.of(timeState.hour, timeState.minute))
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
private fun TimePickerDialogPreview() {
    MaterialTheme {
        TimePickerDialog(
            model = TimePickerDialogModel(hour = 8, minute = 30),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
