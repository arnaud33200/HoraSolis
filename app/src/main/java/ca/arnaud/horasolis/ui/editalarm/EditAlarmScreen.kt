package ca.arnaud.horasolis.ui.editalarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import io.ktor.util.date.WeekDay
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun EditAlarmScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onAction: (EditAlarmUiAction) -> Unit,
    model: EditAlarmScreenModel,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_content_description)
                    )
                }
            }
        },
    ) { innerPadding ->
        when (model) {
            is EditAlarmScreenModel.Content -> EditAlarmContent(
                modifier = Modifier.padding(innerPadding),
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
    model: EditAlarmScreenModel.Content,
    onAction: (EditAlarmUiAction) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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

        EditDayOfWeeks(
            items = model.dayOfWeeks,
            onItemClick = { onAction(EditAlarmUiAction.DayOfWeekClicked(it)) },
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 8.dp),
            onClick = { onAction(EditAlarmUiAction.SaveClicked) },
        ) {
            Text(stringResource(R.string.ok_button))
        }
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
                val text = if (isDay) "\u2600\uFE0F" else "\uD83C\uDF1A"
                Text(
                    text = text,
                )
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
            dayOfWeeks = sampleDayOfWeeks,
        ),
        EditAlarmScreenModel.Content(
            hour = 9,
            minute = 0,
            isDay = false,
            civilTime = "21:12",
            dayOfWeeks = persistentListOf(),
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
            onBackClick = {},
            onAction = {},
        )
    }
}
