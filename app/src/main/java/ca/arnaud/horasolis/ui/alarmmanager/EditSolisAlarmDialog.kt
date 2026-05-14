package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.model.SolisTime
import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel
import ca.arnaud.horasolis.ui.EditDayOfWeeks
import io.ktor.util.date.WeekDay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Parameters for the EditSolisAlarmDialog.
 * Used to create Alarm domain model from.
 *
 * @param hour The hour of the alarm (1-12).
 * @param minute The minute of the alarm (0-59).
 * @param isDay Indicates whether the time is in AM (true) or PM (false).
 * @param toCivilTime lambda to convert and format select solis time to civil time string.
 */
data class EditSolisAlarmDialogModel(
    val id: Int? = null,
    val hour: Int = 3,
    val minute: Int = 45,
    val isDay: Boolean = true,
    val toCivilTime: (hour: Int, minute: Int, isDay: Boolean) -> String,
    val dayOfWeeks: ImmutableList<EditDayOfWeekItemModel>,
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
    model: EditSolisAlarmDialogModel,
    onConfirm: (EditSolisAlarmDialogModel) -> Unit,
    onDismiss: () -> Unit
) {
    var hourState by remember { mutableIntStateOf(model.hour) }
    var minuteState by remember { mutableIntStateOf(model.minute) }
    var isDayState by remember { mutableStateOf(model.isDay) }
    var dayOfWeeks by remember { mutableStateOf(model.dayOfWeeks) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.select_time_title)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CustomTimePicker(
                    hour = hourState,
                    minute = minuteState,
                    isDay = isDayState,
                    toCivilTime = model.toCivilTime,
                    onHourChange = { hourState = it },
                    onMinuteChange = { minuteState = it },
                    onDayNightToggle = { isDayState = it },
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))

                EditDayOfWeeks(
                    items = dayOfWeeks,
                    onItemClick = {
                        dayOfWeeks = dayOfWeeks.map { item ->
                            if (item.text == it.text) {
                                item.copy(selected = !item.selected)
                            } else {
                                item
                            }
                        }.toImmutableList()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val params = model.copy(
                    hour = hourState,
                    minute = minuteState,
                    isDay = isDayState,
                    dayOfWeeks = dayOfWeeks,
                )
                onConfirm(params)
            }) {
                Text(stringResource(id = R.string.ok_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel_button))
            }
        }
    )
}

@Composable
fun CustomTimePicker(
    modifier: Modifier = Modifier,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onDayNightToggle: (Boolean) -> Unit,
    toCivilTime: (hour: Int, minute: Int, isDay: Boolean) -> String,
    hour: Int,
    minute: Int,
    isDay: Boolean,

    ) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Switch(
            checked = isDay,
            onCheckedChange = onDayNightToggle,
            thumbContent = {
                val text = if (isDay) "\u2600\uFE0F" else "\uD83C\uDF1A"
                Text(text = text)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(id = R.string.hour_label, hour))
        Slider(
            value = hour.toFloat(),
            onValueChange = { onHourChange(it.toInt()) },
            valueRange = 1f..12f,
            steps = 10
        )
        Text(stringResource(id = R.string.minute_label, minute))
        Slider(
            value = minute.toFloat(),
            onValueChange = { onMinuteChange(it.toInt()) },
            valueRange = 0f..59f,
            steps = 58
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = toCivilTime(hour, minute, isDay),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@PreviewLightDark
@Composable
private fun TimePickerDialogPreview() {
    MaterialTheme {
        val model = EditSolisAlarmDialogModel(
            hour = 8,
            minute = 30,
            isDay = true,
            toCivilTime = { _, _, _ -> "08:40" },
            dayOfWeeks = WeekDay.entries.map {
                EditDayOfWeekItemModel(
                    text = it.name.substring(0, 3),
                    selected = it == WeekDay.MONDAY || it == WeekDay.WEDNESDAY,
                    data = it
                )
            }.toImmutableList()
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            EditSolisAlarmDialog(
                model = model,
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}
