package ca.arnaud.horasolis.ui.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

data class SolisTimeModel(
    val hours: String,
    val seconds: String,
)

@Composable
fun SolisTime(
    modifier: Modifier = Modifier,
    model: SolisTimeModel,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            text = model.hours,
            style = MaterialTheme.typography.headlineLarge,
        )
        Box(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = model.seconds,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

class SolisTimeModelPreviewProvider : PreviewParameterProvider<SolisTimeModel> {
    override val values = sequenceOf(
        SolisTimeModel(hours = "12 🌞 00", seconds = "30"),
    )
}

@PreviewLightDark
@Composable
private fun SolisTimePreview(
    @PreviewParameter(SolisTimeModelPreviewProvider::class) model: SolisTimeModel,
) {
    HoraSolisTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            SolisTime(model = model)
        }
    }
}
