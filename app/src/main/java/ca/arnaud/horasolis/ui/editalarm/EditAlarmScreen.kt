package ca.arnaud.horasolis.ui.editalarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.EditDayOfWeekItemModel
import ca.arnaud.horasolis.ui.EditDayOfWeeks
import ca.arnaud.horasolis.ui.common.HoraTextField
import ca.arnaud.horasolis.ui.common.HoraTopBar
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import io.ktor.util.date.WeekDay
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun EditAlarmScreen(
    modifier: Modifier = Modifier,
    labelState: TextFieldState,
    onBackClick: () -> Unit,
    onAction: (EditAlarmUiAction) -> Unit,
    model: EditAlarmScreenModel,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HoraTopBar(
                onBack = onBackClick,
                title = stringResource(R.string.edit_alarm_screen_title),
                actions = {
                    IconButton(
                        enabled = (model as? EditAlarmScreenModel.Content)?.saveEnabled == true,
                        onClick = { onAction(EditAlarmUiAction.SaveClicked) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (model) {
            is EditAlarmScreenModel.Content -> EditAlarmContent(
                modifier = Modifier.padding(innerPadding),
                labelState = labelState,
                model = model,
                onAction = onAction,
            )

            EditAlarmScreenModel.Loading -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun EditAlarmContent(
    modifier: Modifier = Modifier,
    labelState: TextFieldState,
    model: EditAlarmScreenModel.Content,
    onAction: (EditAlarmUiAction) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        HoraTextField(
            state = labelState,
            label = stringResource(R.string.alarm_label),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTimePicker(
            hour = model.hour,
            minute = model.minute,
            isDay = model.isDay,
            toCivilTime = { _, _, _ -> model.civilTime },
            onHourChange = { onAction(EditAlarmUiAction.HourChanged(it)) },
            onMinuteChange = { onAction(EditAlarmUiAction.MinuteChanged(it)) },
            onDayNightToggle = { onAction(EditAlarmUiAction.DayNightToggled(it)) },
        )

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))

        ScheduleSection(
            modifier = Modifier.fillMaxWidth(),
            scheduleContent = model.scheduleContent,
            onAction = onAction,
        )

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        SoundSection(
            soundName = model.soundName,
            onPickerClicked = { onAction(EditAlarmUiAction.SoundPickerClicked) },
        )

        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun ScheduleSection(
    modifier: Modifier = Modifier,
    scheduleContent: ScheduleContent,
    onAction: (EditAlarmUiAction) -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    val selectedLabel = when (scheduleContent) {
        is ScheduleContent.Repeating -> stringResource(R.string.alarm_schedule_type_repeating)
        is ScheduleContent.OneTime -> stringResource(R.string.alarm_schedule_type_one_time)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.alarm_schedule_type_repeating)) },
                    onClick = {
                        onAction(EditAlarmUiAction.ScheduleTypeSelected(isRepeating = true))
                        dropdownExpanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.alarm_schedule_type_one_time)) },
                    onClick = {
                        onAction(EditAlarmUiAction.ScheduleTypeSelected(isRepeating = false))
                        dropdownExpanded = false
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (scheduleContent) {
            is ScheduleContent.Repeating -> EditDayOfWeeks(
                items = scheduleContent.dayOfWeeks,
                onItemClick = { onAction(EditAlarmUiAction.DayOfWeekClicked(it)) },
            )
            is ScheduleContent.OneTime -> OneTimeDatePicker(
                selectedDateLabel = scheduleContent.selectedDate,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun OneTimeDatePicker(
    selectedDateLabel: String,
    onAction: (EditAlarmUiAction) -> Unit,
) {
    TextButton(onClick = { onAction(EditAlarmUiAction.DatePickerClicked) }) {
        Text(
            text = selectedDateLabel,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
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
                val text = if (isDay) "☀️" else "🌚"
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

@Composable
private fun SoundSection(
    modifier: Modifier = Modifier,
    soundName: String,
    onPickerClicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.alarm_sound_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = soundName,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        TextButton(onClick = onPickerClicked) {
            Text(stringResource(R.string.alarm_sound_change))
        }
    }
}

private class EditAlarmScreenPreviewProvider : PreviewParameterProvider<EditAlarmScreenModel> {

    private val sampleDayOfWeeks = WeekDay.entries.map { weekDay ->
        EditDayOfWeekItemModel(
            text = weekDay.name.substring(0, 3),
            selected = weekDay == WeekDay.MONDAY || weekDay == WeekDay.WEDNESDAY || weekDay == WeekDay.FRIDAY,
            data = weekDay,
        )
    }.toImmutableList()

    override val values = sequenceOf(
        EditAlarmScreenModel.Content(
            hour = 6,
            minute = 30,
            isDay = true,
            civilTime = "07:45",
            scheduleContent = ScheduleContent.Repeating(sampleDayOfWeeks),
            soundName = "Cesium",
            saveEnabled = true,
        ),
        EditAlarmScreenModel.Content(
            hour = 9,
            minute = 0,
            isDay = false,
            civilTime = "21:12",
            scheduleContent = ScheduleContent.OneTime(selectedDate = "May 20"),
            soundName = "Default",
        ),
        EditAlarmScreenModel.Content(
            hour = 3,
            minute = 0,
            isDay = true,
            civilTime = "06:00",
            scheduleContent = ScheduleContent.Repeating(persistentListOf()),
            soundName = "Default",
        ),
        EditAlarmScreenModel.Loading,
    )
}

@PreviewLightDark
@Composable
private fun EditAlarmScreenPreview(
    @PreviewParameter(EditAlarmScreenPreviewProvider::class) model: EditAlarmScreenModel,
) {
    HoraSolisTheme {
        EditAlarmScreen(
            model = model,
            labelState = rememberTextFieldState("Morning"),
            onBackClick = {},
            onAction = {},
        )
    }
}
