package ca.arnaud.horasolis.ui.logviewer

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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Card
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

sealed interface LogViewerScreenModel {

    data object Empty : LogViewerScreenModel

    data class Content(
        val items: ImmutableList<LogItemModel>,
    ) : LogViewerScreenModel
}

data class LogItemModel(
    val id: Long,
    val alarmId: Int,
    val typeLabel: String,
    val timestampLabel: String,
    val detailLabel: String?,
)

@Composable
fun LogViewerScreen(
    modifier: Modifier = Modifier,
    model: LogViewerScreenModel,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HoraTopBar(
                onBack = onBackClick,
                title = stringResource(R.string.log_viewer_screen_title),
                actions = if (model is LogViewerScreenModel.Content) {
                    {
                        IconButton(onClick = onClearClick) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.log_viewer_clear_button),
                            )
                        }
                    }
                } else null,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (model) {
                is LogViewerScreenModel.Empty -> EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                )
                is LogViewerScreenModel.Content -> LogList(
                    modifier = Modifier.navigationBarsPadding(),
                    items = model.items,
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
        text = stringResource(R.string.log_viewer_empty_message),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    )
}

@Composable
private fun LogList(
    modifier: Modifier = Modifier,
    items: ImmutableList<LogItemModel>,
) {
    LazyColumn(modifier = modifier) {
        items(items = items, key = { it.id }) { item ->
            LogItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                item = item,
            )
        }
    }
}

@Composable
private fun LogItem(
    modifier: Modifier = Modifier,
    item: LogItemModel,
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.log_viewer_alarm_id_label, item.alarmId),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = item.typeLabel,
                    style = MaterialTheme.typography.bodyLarge,
                )
                item.detailLabel?.let { detail ->
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = item.timestampLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// region Preview

private class LogViewerScreenPreviewProvider : PreviewParameterProvider<LogViewerScreenModel> {
    override val values = sequenceOf(
        LogViewerScreenModel.Content(
            items = persistentListOf(
                LogItemModel(
                    id = 1,
                    alarmId = 1,
                    typeLabel = "Ringing",
                    timestampLabel = "May 26, 6:30 AM",
                    detailLabel = null,
                ),
                LogItemModel(
                    id = 2,
                    alarmId = 1,
                    typeLabel = "Scheduled",
                    timestampLabel = "May 25, 11:00 PM",
                    detailLabel = "May 26 6:30 AM",
                ),
                LogItemModel(
                    id = 3,
                    alarmId = 2,
                    typeLabel = "Cancelled",
                    timestampLabel = "May 25, 10:00 AM",
                    detailLabel = null,
                ),
            ),
        ),
        LogViewerScreenModel.Empty,
    )
}

@PreviewLightDark
@Composable
private fun LogViewerScreenPreview(
    @PreviewParameter(LogViewerScreenPreviewProvider::class) model: LogViewerScreenModel,
) {
    HoraSolisTheme {
        LogViewerScreen(
            model = model,
            onBackClick = {},
            onClearClick = {},
        )
    }
}

// endregion
