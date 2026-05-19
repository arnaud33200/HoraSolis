package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.clock.SolisClockDialogModel
import ca.arnaud.horasolis.ui.clock.SolisClockModel
import ca.arnaud.horasolis.ui.clock.SolisClockWithTime
import ca.arnaud.horasolis.ui.clock.SolisTimeModel
import ca.arnaud.horasolis.ui.common.HoraTopBar
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import ca.arnaud.horasolis.ui.theme.Typography
import kotlinx.collections.immutable.persistentListOf

sealed interface AlarmManagerScreenModel {

    val snackMessage: String?

    data class Loading(
        override val snackMessage: String? = null,
    ) : AlarmManagerScreenModel

    data class MissingLocation(
        override val snackMessage: String? = null,
    ) : AlarmManagerScreenModel

    data class Content(
        override val snackMessage: String? = null,
        val list: AlarmListModel = AlarmListModel(),
    ) : AlarmManagerScreenModel
}

@Composable
fun AlarmManagerScreen(
    modifier: Modifier = Modifier,
    onSnackbarDismissed: () -> Unit,
    onSolisViewerClick: () -> Unit,
    onLocationClick: () -> Unit,
    onAddClick: () -> Unit,
    onAlarmDeleteClick: (AlarmItemModel) -> Unit,
    onAlarmItemClick: (AlarmItemModel) -> Unit,
    onAlarmToggleClick: (AlarmItemModel, Boolean) -> Unit,
    model: AlarmManagerScreenModel,
    clockModel: SolisClockDialogModel = SolisClockDialogModel.Loading,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(model.snackMessage) {
        model.snackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onSnackbarDismissed()
        }
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HoraTopBar(
                navigationIcon = {
                    IconButton(onClick = onSolisViewerClick) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLocationClick) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.location_content_description),
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (model !is AlarmManagerScreenModel.MissingLocation) {
                BottomBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .navigationBarsPadding(),
                    onAddClick = onAddClick,
                )
            }
        }
    ) { innerPadding ->
        when (model) {
            is AlarmManagerScreenModel.Content -> Content(
                modifier = Modifier
                    .padding(innerPadding),
                model = model,
                onAlarmDeleteClick = onAlarmDeleteClick,
                onAlarmItemClick = onAlarmItemClick,
                onAlarmToggleClick = onAlarmToggleClick,
                clockModel = clockModel,
            )

            is AlarmManagerScreenModel.MissingLocation -> MissingLocation(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                onLocationClick = onLocationClick,
            )

            is AlarmManagerScreenModel.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun MissingLocation(
    modifier: Modifier = Modifier,
    onLocationClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.alarm_manager_screen_missing_location_message),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLocationClick,
        ) {
            Text(
                text = stringResource(R.string.alarm_manager_screen_missing_location_button),
            )
        }
    }
}


@Composable
private fun Content(
    modifier: Modifier = Modifier,
    onAlarmDeleteClick: (AlarmItemModel) -> Unit,
    onAlarmItemClick: (AlarmItemModel) -> Unit,
    onAlarmToggleClick: (AlarmItemModel, Boolean) -> Unit,
    model: AlarmManagerScreenModel.Content,
    clockModel: SolisClockDialogModel,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SolisClockWithTime(
            model = clockModel,
            clockSize = 175.dp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AlarmList(
            modifier = Modifier
                .fillMaxWidth(),
            model = model.list,
            onDelete = onAlarmDeleteClick,
            onEdit = onAlarmItemClick,
            onToggle = onAlarmToggleClick,
        )
    }
}


@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onAddClick,

        ) {
        Text(
            text = stringResource(R.string.alarm_manager_screen_add_button),
            style = Typography.bodyLarge
        )
    }
}

@PreviewLightDark
@Composable
private fun AlarmManagerScreenPreview() {
    HoraSolisTheme {
        val sampleList = AlarmListModel(
            items = persistentListOf(
                AlarmItemModel(
                    id = 1,
                    title = "5 \u2600\uFE0F 06",
                    label = "Morning",
                    civilTime = "6:30 AM",
                    isEnabled = true,
                    schedule = "Monday to Friday"
                ),
                AlarmItemModel(
                    id = 2,
                    title = "10 \uD83C\uDF1A 54",
                    label = null,
                    civilTime = "11:00 PM",
                    isEnabled = false,
                    schedule = "Week-end"
                ),
                AlarmItemModel(
                    id = 3,
                    title = "12 \u2600\uFE0F 00",
                    label = null,
                    civilTime = "12:00 PM",
                    isEnabled = true,
                    schedule = "Every day"
                ),
            )
        )
        AlarmManagerScreen(
            model = AlarmManagerScreenModel.Content(
                list = sampleList
            ),
            clockModel = SolisClockDialogModel.Content(
                time = SolisTimeModel(
                    hours = "05 \u2600\uFE0F 06",
                    seconds = "35",
                ),
                location = "Toronto, Canada",
                clock = SolisClockModel(
                    dayStartAngle = -90f,
                    dayEndAngle = 200f,
                    needleAngle = 30f,
                ),
            ),
            onSnackbarDismissed = {},
            onSolisViewerClick = {},
            onLocationClick = {},
            onAlarmDeleteClick = {},
            onAddClick = {},
            onAlarmItemClick = {},
            onAlarmToggleClick = { _, _ -> },
        )
    }
}
