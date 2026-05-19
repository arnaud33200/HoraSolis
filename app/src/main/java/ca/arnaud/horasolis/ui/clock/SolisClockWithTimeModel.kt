package ca.arnaud.horasolis.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

sealed interface SolisClockWithTimeModel {

    data object Loading : SolisClockWithTimeModel
    data object Error : SolisClockWithTimeModel

    data class Content(
        val time: SolisTimeModel,
        val location: String,
        val clock: SolisClockModel,
    ) : SolisClockWithTimeModel
}

@Composable
fun SolisClockWithTime(
    modifier: Modifier = Modifier,
    model: SolisClockWithTimeModel,
    clockSize: Dp = 200.dp,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (model) {
            is SolisClockWithTimeModel.Loading -> {
                CircularProgressIndicator()
            }

            is SolisClockWithTimeModel.Content -> {
                SolisTime(model = model.time)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = model.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                SolisClock(
                    model = model.clock,
                    modifier = Modifier.size(clockSize)
                )
            }

            SolisClockWithTimeModel.Error -> {
                Text(text = stringResource(id = R.string.solis_clock_error_message))
            }
        }
    }
}

class SolisClockDialogModelPreviewProvider : PreviewParameterProvider<SolisClockWithTimeModel> {
    override val values = sequenceOf(
        SolisClockWithTimeModel.Content(
            time = SolisTimeModel(hours = "12 🌞 00", seconds = "30"),
            location = "Toronto, Canada",
            clock = SolisClockModel(
                dayStartAngle = -90f,
                dayEndAngle = 200f,
                needleAngle = 30f,
            )
        ),
        SolisClockWithTimeModel.Loading,
        SolisClockWithTimeModel.Error
    )
}

@PreviewLightDark
@Composable
private fun SolisClockWithTimePreview(
    @PreviewParameter(SolisClockDialogModelPreviewProvider::class) model: SolisClockWithTimeModel,
) {
    HoraSolisTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SolisClockWithTime(
                model = model,
            )
        }
    }
}
