package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AlarmListModel(
    val items: ImmutableList<AlarmItemModel> = persistentListOf(),
)

data class AlarmItemModel(
    val id: Int,
    val title: String,
    val label: String?,
    val civilTime: String,
    val isEnabled: Boolean = true,
    val schedule: String?,
)

@Composable
fun AlarmList(
    modifier: Modifier = Modifier,
    onDelete: (AlarmItemModel) -> Unit,
    onEdit: (AlarmItemModel) -> Unit,
    onToggle: (AlarmItemModel, Boolean) -> Unit,
    model: AlarmListModel,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(model.items, key = { it.id }) { item ->
            AlarmListItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onEdit = onEdit,
                onDelete = onDelete,
                onToggle = onToggle,
            )
        }
    }
}

@Composable
private fun AlarmListItem(
    modifier: Modifier = Modifier,
    onEdit: (AlarmItemModel) -> Unit,
    onDelete: (AlarmItemModel) -> Unit,
    onToggle: (AlarmItemModel, Boolean) -> Unit,
    item: AlarmItemModel,
) {
    val defaultColors = CardDefaults.cardColors()
    val alpha = if (item.isEnabled) 1f else 0.4f
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = defaultColors.containerColor.copy(alpha = alpha)
        ),
        onClick = { onEdit(item) },
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp, vertical = 8.dp,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    modifier = Modifier
                        .alpha(alpha)
                        .weight(1f),
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .alpha(alpha),
                    text = item.civilTime,
                    style = MaterialTheme.typography.bodySmall,
                )
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = LocalContentColor.current.copy(alpha = 0.6f),
                        contentDescription = "Delete alarm",
                    )
                }
                Switch(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    checked = item.isEnabled,
                    onCheckedChange = { onToggle(item, it) },
                )
            }

            item.label?.let { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            item.schedule?.let { schedule ->
                Text(
                    modifier = Modifier.alpha(alpha),
                    text = schedule,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AlarmListPreview() {
    HoraSolisTheme {
        Surface {
            val sampleModel = AlarmListModel(
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
                        id = 2,
                        title = "10 \uD83C\uDF1A 54",
                        label = null,
                        civilTime = "11:00 PM",
                        isEnabled = true,
                        schedule = null,
                    ),
                    AlarmItemModel(
                        id = 3,
                        title = "12 \u2600\uFE0F 00",
                        label = "Noon",
                        civilTime = "12:00 PM",
                        isEnabled = true,
                        schedule = "Every day"
                    ),
                )
            )
            AlarmList(
                model = sampleModel,
                onDelete = {},
                onEdit = {},
                onToggle = { _, _ -> }
            )
        }
    }
}
