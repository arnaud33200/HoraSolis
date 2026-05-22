package ca.arnaud.horasolis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import io.ktor.util.date.WeekDay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class EditDayOfWeekItemModel(
    val text: String,
    val selected: Boolean = false,
    val data: WeekDay,
)

@Composable
fun EditDayOfWeeks(
    modifier: Modifier = Modifier,
    onItemClick: (EditDayOfWeekItemModel) -> Unit,
    items: ImmutableList<EditDayOfWeekItemModel>,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items.forEach { item ->
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onItemClick(item) }
                    .background(
                        color = if (item.selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = MaterialTheme.shapes.small,
                    )
                    .border(
                        width = 0.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(vertical = 12.dp),
                text = item.text,
                textAlign = TextAlign.Center,
                color = if (item.selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    LocalContentColor.current.copy(alpha = 0.6f)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (item.selected) FontWeight.Bold else null,
            )
        }
    }
}

private class EditDayOfWeeksPreviewProvider : PreviewParameterProvider<ImmutableList<EditDayOfWeekItemModel>> {

    private fun items(vararg selected: WeekDay) = WeekDay.entries.map { weekDay ->
        EditDayOfWeekItemModel(
            text = weekDay.name.substring(0, 3),
            selected = weekDay in selected,
            data = weekDay,
        )
    }.toImmutableList()

    override val values = sequenceOf(
        items(WeekDay.MONDAY, WeekDay.WEDNESDAY, WeekDay.FRIDAY),
        items(*WeekDay.entries.toTypedArray()),
        items(),
    )
}

@PreviewLightDark
@Composable
private fun EditDayOfWeeksPreview(
    @PreviewParameter(EditDayOfWeeksPreviewProvider::class) items: ImmutableList<EditDayOfWeekItemModel>,
) {
    HoraSolisTheme {
        Surface {
            EditDayOfWeeks(
                modifier = Modifier.padding(8.dp),
                items = items,
                onItemClick = {},
            )
        }
    }
}