package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.domain.model.SolisTime

/**
 * Parameters for the EditSolisAlarmDialog.
 * Used to create Alarm domain model from.
 *
 * @param hour The hour of the alarm (1-12).
 * @param minute The minute of the alarm (0-59).
 * @param isDay Indicates whether the time is in AM (true) or PM (false).
 */
data class EditSolisAlarmParams(
    val id: Int? = null,
    val hour: Int = 3,
    val minute: Int = 45,
    val isDay: Boolean = true,
) {

    fun getSolisTime(): SolisTime {
        return SolisTime(
            hour = hour,
            minute = minute,
            type = if (isDay) SolisTime.Type.Day else SolisTime.Type.Night,
        )
    }
}

@Composable
fun EditSolisAlarmDialog(
    model: EditSolisAlarmParams = EditSolisAlarmParams(),
    onConfirm: (EditSolisAlarmParams) -> Unit,
    onDismiss: () -> Unit
) {
    var hourState by remember { mutableIntStateOf(model.hour) }
    var minuteState by remember { mutableIntStateOf(model.minute) }
    var isDayState by remember { mutableStateOf(model.isDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Column {
                CustomTimePicker(
                    hour = hourState,
                    minute = minuteState,
                    isDay = isDayState,
                    onHourChange = { hourState = it },
                    onMinuteChange = { minuteState = it },
                    onDayNightToggle = { isDayState = it },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val params = model.copy(
                    hour = hourState,
                    minute = minuteState,
                    isDay = isDayState,
                )
                onConfirm(params)
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

@Composable
fun CustomTimePicker(
    hour: Int,
    minute: Int,
    isDay: Boolean,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onDayNightToggle: (Boolean) -> Unit,
) {
    Column {
        Switch(
            checked = isDay,
            onCheckedChange = onDayNightToggle,
            thumbContent = {
                val text = if (isDay) "\u2600\uFE0F" else "\uD83C\uDF1A"
                Text(text = text)
            }
        )

        Spacer(modifier = Modifier.width(16.dp))
        Text("Hour: $hour")
        Slider(
            value = hour.toFloat(),
            onValueChange = { onHourChange(it.toInt()) },
            valueRange = 1f..12f,
            steps = 10
        )
        Text("Minute: $minute")
        Slider(
            value = minute.toFloat(),
            onValueChange = { onMinuteChange(it.toInt()) },
            valueRange = 0f..59f,
            steps = 58
        )
    }
}

@Preview
@Composable
private fun TimePickerDialogPreview() {
    MaterialTheme {
        EditSolisAlarmDialog(
            model = EditSolisAlarmParams(hour = 8, minute = 30),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
