package ca.arnaud.horasolis.ui.timelist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import ca.arnaud.horasolis.ui.theme.Typography
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TimeCheckList(
    modifier: Modifier = Modifier,
    dayTimes: TimeListModel,
    nightTimes: TimeListModel,
    loading: Boolean,
    onTimeChecked: (TimeItem, Boolean) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        TimeListColumn(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            header = "\uD83C\uDF1E",
            model = dayTimes,
            loading = loading,
            onTimeChecked = onTimeChecked
        )
        TimeListColumn(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            header = "\uD83C\uDF1A",
            model = nightTimes,
            loading = loading,
            onTimeChecked = onTimeChecked,
            night = true,
        )
    }
}

@Composable
internal fun TimeListColumn(
    modifier: Modifier = Modifier,
    model: TimeListModel,
    header: String,
    loading: Boolean,
    night: Boolean = false,
    onTimeChecked: (TimeItem, Boolean) -> Unit
) {
    val headerModifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxWidth()
    LazyColumn(
        modifier = modifier,
    ) {
        stickyHeader {
            ListHeader(
                modifier = headerModifier,
                text = header,
                description = model.description,
            )
        }
        items(model.times) { timeItem ->
            TimeItemRow(
                modifier = Modifier.padding(bottom = 4.dp),
                timeItem = timeItem,
                loading = loading,
                onTimeChecked = onTimeChecked,
                night = night,
            )
        }
    }
}

@Composable
internal fun ListHeader(
    modifier: Modifier = Modifier,
    text: String,
    description: String,
) {
    Column(
        modifier = modifier.background(HoraSolisTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = text,
            style = Typography.headlineMedium,
        )
        Text(
            text = description,
            style = Typography.bodyMedium,
        )
    }
}

@Composable
internal fun TimeItemRow(
    modifier: Modifier = Modifier,
    onTimeChecked: (TimeItem, Boolean) -> Unit,
    timeItem: TimeItem,
    loading: Boolean,
    night: Boolean,
) {
    val containerColor = if (night) {
        HoraSolisTheme.colors.secondaryContainer
    } else {
        HoraSolisTheme.colors.surfaceContainer
    }
    val contentColor = if (night) {
        HoraSolisTheme.colors.onSecondaryContainer
    } else {
        HoraSolisTheme.colors.onSurface
    }
    val border = if (timeItem.highlight) BorderStroke(3.dp, HoraSolisTheme.colors.primary) else null
    val cardColors = CardDefaults.cardColors(
        containerColor = if (timeItem.highlight) contentColor else containerColor,
        contentColor = if (timeItem.highlight) containerColor else contentColor,
    )
    Card(
        modifier = modifier,
        border = border,
        colors = cardColors,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = timeItem.checked,
                colors = CheckboxDefaults.colors().copy(
                    uncheckedBorderColor = cardColors.contentColor,
                ),
                onCheckedChange = { isChecked ->
                    onTimeChecked(timeItem, isChecked)
                }
            )
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = timeItem.label,
                    style = Typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.placeholder(
                        visible = loading,
                        color = HoraSolisTheme.colors.onSurface.copy(alpha = 0.1f),
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                    text = timeItem.hour,
                    style = Typography.bodyLarge,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TimeCheckListPreview() {
    HoraSolisTheme {
        Surface {
            TimeCheckList(
                dayTimes = TimeListModel(
                    description = "15:34",
                    times = List(12) { i ->
                        TimeItem(
                            number = i + 1,
                            label = "Day ${i + 1}",
                            hour = String.format("%02d:00", i),
                            checked = (i % 3 == 0),
                            highlight = (i == 6),
                        )
                    }.toImmutableList()
                ),
                nightTimes = TimeListModel(
                    description = "08:45",
                    times = List(12) { i ->
                        TimeItem(
                            number = i + 1,
                            label = "Night ${i + 1}",
                            hour = String.format("%02d:00", i),
                            checked = (i % 3 == 0),
                            highlight = (i == 6),
                        )
                    }.toImmutableList()
                ),
                loading = false,
                onTimeChecked = { _, _ -> }
            )
        }
    }
}
