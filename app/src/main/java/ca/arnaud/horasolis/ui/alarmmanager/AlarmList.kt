package ca.arnaud.horasolis.ui.alarmmanager

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
    val id: Long,
    val title: String,
)

@Composable
fun AlarmList(
    modifier: Modifier = Modifier,
    model: AlarmListModel,
    onDelete: (AlarmItemModel) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(model.items, key = { it.id }) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete alarm"
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
                    AlarmItemModel(id = 1, title = "Wake up"),
                    AlarmItemModel(id = 2, title = "Meeting"),
                    AlarmItemModel(id = 3, title = "Workout")
                )
            )
            AlarmList(
                model = sampleModel,
                onDelete = {}
            )
        }
    }
}
