package ca.arnaud.horasolis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

data class DayOfWeekItemModel(
    val text: String,
    val selected: Boolean = false,
)

@Composable
fun DayOfWeekList(
    modifier: Modifier = Modifier,
    items: ImmutableList<DayOfWeekItemModel>,
) {
    Row(modifier = modifier) {
        items.forEach { item ->
            Text(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
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
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                text = item.text,
                color = if (item.selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    LocalContentColor.current.copy(alpha = 0.6f)
                },
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }

}