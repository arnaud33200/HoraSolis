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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.DayOfWeekItemModel
import ca.arnaud.horasolis.ui.DayOfWeekList
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AlarmListModel(
    val items: ImmutableList<AlarmItemModel> = persistentListOf(),
)

data class AlarmItemModel(
    val id: Int,
    val title: String,
    val civilTime: String,
    val isEnabled: Boolean = true,
    val dayOfWeeks: ImmutableList<DayOfWeekItemModel>,
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
    item: AlarmItemModel,
    onEdit: (AlarmItemModel) -> Unit,
    onDelete: (AlarmItemModel) -> Unit,
    onToggle: (AlarmItemModel, Boolean) -> Unit,
) {
    Card(
        onClick = { onEdit(item) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = item.civilTime,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = LocalContentColor.current.copy(alpha = 0.6f),
                        contentDescription = "Delete alarm",
                    )
                }
                Switch(
                    checked = item.isEnabled,
                    onCheckedChange = { onToggle(item, it) },
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            DayOfWeekList(
                modifier = Modifier.padding(bottom = 8.dp),
                items = item.dayOfWeeks,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AlarmListPreview() {
    HoraSolisTheme {
        Surface {
            val sampleDayOfWeeks = persistentListOf(
                DayOfWeekItemModel("Mon", selected = true),
                DayOfWeekItemModel("Tue"),
                DayOfWeekItemModel("Wed", selected = true),
                DayOfWeekItemModel("Thu"),
                DayOfWeekItemModel("Fri", selected = true),
                DayOfWeekItemModel("Sat"),
                DayOfWeekItemModel("Sun"),
            )
            val sampleModel = AlarmListModel(
                items = persistentListOf(
                    AlarmItemModel(
                        id = 1,
                        title = "5 \u2600\uFE0F 06",
                        "12:56",
                        true,
                        sampleDayOfWeeks
                    ), // 5 ☀️ 06
                    AlarmItemModel(
                        id = 2,
                        title = "10 \uD83C\uDF1A 54",
                        "12:56",
                        false,
                        sampleDayOfWeeks
                    ), // 10 🌚 54
                    AlarmItemModel(
                        id = 3,
                        title = "12 \u2600\uFE0F 00",
                        "12:56",
                        true,
                        sampleDayOfWeeks
                    ) // 12 ☀️ 00
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
