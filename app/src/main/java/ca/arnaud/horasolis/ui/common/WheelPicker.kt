package ca.arnaud.horasolis.ui.common

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.filter

data class WheelPickerItem(
    val text: String,
    val data: Int,
)

@Composable
fun WheelPicker(
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit,
    items: ImmutableList<WheelPickerItem>,
    selectedItem: WheelPickerItem,
    visibleItemCount: Int = 5,
    itemHeight: Dp = 48.dp,
) {
    val selectedIndex = remember(selectedItem, items) {
        items.indexOfFirst { it == selectedItem }.coerceAtLeast(0)
    }
    val paddingCount = visibleItemCount / 2
    val totalHeight = itemHeight * visibleItemCount
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val currentIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    LaunchedEffect(selectedIndex) {
        if (listState.firstVisibleItemIndex != selectedIndex) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { !it }
            .collect {
                val snapped = listState.firstVisibleItemIndex
                val snappedItem = items.getOrNull(snapped) ?: return@collect
                if (snappedItem != selectedItem) {
                    onValueChange(snappedItem.data)
                }
            }
    }

    Box(modifier = modifier.height(totalHeight)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(paddingCount.toFloat()))
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.weight(paddingCount.toFloat()))
        }
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * paddingCount),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(items) { index, item ->
                val distance = kotlin.math.abs(index - currentIndex)
                val alpha = when (distance) {
                    0 -> 1f
                    1 -> 0.55f
                    else -> 0.25f
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.text,
                        style = if (distance == 0) MaterialTheme.typography.headlineSmall
                        else MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.alpha(alpha),
                    )
                }
            }
        }
    }
}
