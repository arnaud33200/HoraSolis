package ca.arnaud.horasolis.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

@Composable
fun HoraTopBar(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    title: String? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_content_description),
                )
            }
        }

        if (title != null) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        actions?.invoke()
    }
}

private data class HoraTopBarPreviewData(
    val showBack: Boolean,
    val title: String?,
    val showActions: Boolean,
)

private class HoraTopBarPreviewProvider : PreviewParameterProvider<HoraTopBarPreviewData> {

    override val values = sequenceOf(
        HoraTopBarPreviewData(showBack = false, title = null, showActions = false),
        HoraTopBarPreviewData(showBack = true, title = null, showActions = false),
        HoraTopBarPreviewData(showBack = false, title = "Alarm Manager", showActions = true),
        HoraTopBarPreviewData(showBack = true, title = "Location Manager", showActions = true),
    )
}

@PreviewLightDark
@Composable
private fun HoraTopBarPreview(
    @PreviewParameter(HoraTopBarPreviewProvider::class) data: HoraTopBarPreviewData,
) {
    HoraSolisTheme {
        HoraTopBar(
            onBack = if (data.showBack) { {} } else null,
            title = data.title,
            actions = if (data.showActions) {
                { IconButton(onClick = {}) { Icon(Icons.Default.LocationOn, contentDescription = null) } }
            } else null,
        )
    }
}
