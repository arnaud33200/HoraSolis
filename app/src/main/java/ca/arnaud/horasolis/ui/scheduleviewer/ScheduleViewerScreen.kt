package ca.arnaud.horasolis.ui.scheduleviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import ca.arnaud.horasolis.ui.common.HoraTopBar
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface ScheduleViewerScreenModel {

    val isRefreshing: Boolean

    data class Empty(
        override val isRefreshing: Boolean = false,
    ) : ScheduleViewerScreenModel

    data class Content(
        val items: ImmutableList<ScheduleItemModel>,
        override val isRefreshing: Boolean = false,
    ) : ScheduleViewerScreenModel
}

data class ScheduleItemModel(
    val alarmId: Int,
    val dateLabel: String,
    val timeLabel: String,
)

@Composable
fun ScheduleViewerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    model: ScheduleViewerScreenModel,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HoraTopBar(
                onBack = onBackClick,
                title = stringResource(R.string.schedule_viewer_screen_title),
                actions = {
                    IconButton(onClick = onRefreshClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (model) {
                is ScheduleViewerScreenModel.Empty -> EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                )

                is ScheduleViewerScreenModel.Content -> ScheduleList(
                    modifier = Modifier.navigationBarsPadding(),
                    items = model.items,
                )
            }
            if (model.isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.schedule_viewer_empty_message),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    )
}

@Composable
private fun ScheduleList(
    modifier: Modifier = Modifier,
    items: ImmutableList<ScheduleItemModel>,
) {
    LazyColumn(modifier = modifier) {
        items(items = items, key = { it.alarmId }) { item ->
            ScheduleItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                item = item,
            )
        }
    }
}

@Composable
private fun ScheduleItem(
    modifier: Modifier = Modifier,
    item: ScheduleItemModel,
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.schedule_viewer_alarm_id_label, item.alarmId),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = item.dateLabel,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Text(
                text = item.timeLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// endregion

// region Preview

private class ScheduleViewerScreenPreviewProvider :
    PreviewParameterProvider<ScheduleViewerScreenModel> {

    override val values = sequenceOf(
        ScheduleViewerScreenModel.Content(
            items = persistentListOf(
                ScheduleItemModel(alarmId = 1, dateLabel = "Monday May 26", timeLabel = "6:30 AM"),
                ScheduleItemModel(
                    alarmId = 2,
                    dateLabel = "Tuesday May 27",
                    timeLabel = "11:00 PM"
                ),
                ScheduleItemModel(
                    alarmId = 3,
                    dateLabel = "Wednesday May 28",
                    timeLabel = "12:00 PM"
                ),
            ),
        ),
        ScheduleViewerScreenModel.Empty(),
    )
}

@PreviewLightDark
@Composable
private fun ScheduleViewerScreenPreview(
    @PreviewParameter(ScheduleViewerScreenPreviewProvider::class) model: ScheduleViewerScreenModel,
) {
    HoraSolisTheme {
        ScheduleViewerScreen(
            model = model,
            onBackClick = {},
            onRefreshClick = {},
        )
    }
}
