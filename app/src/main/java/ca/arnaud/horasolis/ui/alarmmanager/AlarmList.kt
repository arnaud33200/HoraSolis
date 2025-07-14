package ca.arnaud.horasolis.ui.alarmmanager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
)

@Composable
fun AlarmList(
    modifier: Modifier = Modifier,
    onDelete: (AlarmItemModel) -> Unit,
    onEdit: (AlarmItemModel) -> Unit,
    model: AlarmListModel,
) {
    LazyColumn(modifier = modifier) {
        items(model.items, key = { it.id }) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onEdit(item) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = LocalContentColor.current.copy(alpha = 0.6f),
                        contentDescription = "Delete alarm",
                    )
                }
            }
            Divider()
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
                    AlarmItemModel(id = 1, title = "5 \u2600\uFE0F 06"), // 5 ☀️ 06
                    AlarmItemModel(id = 2, title = "10 \uD83C\uDF1A 54"), // 10 🌚 54
                    AlarmItemModel(id = 3, title = "12 \u2600\uFE0F 00") // 12 ☀️ 00
                )
            )
            AlarmList(
                model = sampleModel,
                onDelete = {},
                onEdit = {}
            )
        }
    }
}
