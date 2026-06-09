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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
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
import ca.arnaud.horasolis.ui.common.WheelPicker
import ca.arnaud.horasolis.ui.common.WheelPickerItem
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
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(innerPadding),
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
        modifier = modifier.verticalScroll(rememberScrollState()),
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
            model = model,
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

        Spacer(modifier = Modifier.height(8.dp))

        SoundSection(
            modifier = Modifier.fillMaxWidth(),
            soundName = model.soundName,
            onPickerClicked = { onAction(EditAlarmUiAction.SoundPickerClicked) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        VibrationSection(
            vibrationEnabled = model.vibrationEnabled,
            onToggle = { onAction(EditAlarmUiAction.VibrationToggled(it)) },
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
private fun CustomTimePicker(
    modifier: Modifier = Modifier,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onDayNightToggle: (Boolean) -> Unit,
    model: EditAlarmScreenModel.Content,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            WheelPicker(
                modifier = Modifier.weight(1f),
                items = model.hourItems,
                selectedItem = model.selectedHourItem,
                onValueChange = { onHourChange(it) },
                itemHeight = 40.dp,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = model.isDay,
                onCheckedChange = onDayNightToggle,
                thumbContent = {
                    val text = if (model.isDay) "☀️" else "🌚"
                    Text(text = text)
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            WheelPicker(
                modifier = Modifier.weight(1f),
                items = model.minuteItems,
                selectedItem = model.selectedMinuteItem,
                onValueChange = { onMinuteChange(it) },
                itemHeight = 40.dp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = model.civilTime,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = stringResource(R.string.civil_time_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.CenterHorizontally),
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
        modifier = modifier,
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

@Composable
private fun VibrationSection(
    modifier: Modifier = Modifier,
    onToggle: (Boolean) -> Unit,
    vibrationEnabled: Boolean,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.alarm_vibration_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = vibrationEnabled,
            onCheckedChange = onToggle,
        )
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

    private val sampleHourItems =
        (0..11).map { WheelPickerItem(it.toString(), it + 1) }.toImmutableList()
    private val sampleMinuteItems =
        (0..59).map { WheelPickerItem(it.toString().padStart(2, '0'), it) }.toImmutableList()

    override val values = sequenceOf(
        EditAlarmScreenModel.Content(
            hourTitle = "Hour: 6",
            hourItems = sampleHourItems,
            selectedHourItem = sampleHourItems[5],
            minuteTitle = "Minute: 30",
            minuteItems = sampleMinuteItems,
            selectedMinuteItem = sampleMinuteItems[30],
            isDay = true,
            civilTime = "07:45",
            scheduleContent = ScheduleContent.Repeating(sampleDayOfWeeks),
            soundName = "Cesium",
            vibrationEnabled = true,
            saveEnabled = true,
        ),
        EditAlarmScreenModel.Content(
            hourTitle = "Hour: 9",
            hourItems = sampleHourItems,
            selectedHourItem = sampleHourItems[8],
            minuteTitle = "Minute: 0",
            minuteItems = sampleMinuteItems,
            selectedMinuteItem = sampleMinuteItems[0],
            isDay = false,
            civilTime = "21:12",
            scheduleContent = ScheduleContent.OneTime(selectedDate = "May 20"),
            soundName = "Default",
            vibrationEnabled = false,
        ),
        EditAlarmScreenModel.Content(
            hourTitle = "Hour: 3",
            hourItems = sampleHourItems,
            selectedHourItem = sampleHourItems[2],
            minuteTitle = "Minute: 0",
            minuteItems = sampleMinuteItems,
            selectedMinuteItem = sampleMinuteItems[0],
            isDay = true,
            civilTime = "06:00",
            scheduleContent = ScheduleContent.Repeating(persistentListOf()),
            soundName = "Default",
            vibrationEnabled = true,
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
