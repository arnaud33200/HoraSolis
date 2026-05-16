package ca.arnaud.horasolis.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

sealed interface SolisClockDialogModel {

    data object Loading : SolisClockDialogModel
    data object Error : SolisClockDialogModel

    data class Content(
        val solisHours: String,
        val solisSeconds: String,
        val location: String,
        val clock: SolisClockModel,
    ) : SolisClockDialogModel
}

@Composable
fun SolisClockWithTime(
    modifier: Modifier = Modifier,
    model: SolisClockDialogModel,
    clockSize: Dp = 200.dp,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (model) {
            is SolisClockDialogModel.Loading -> {
                CircularProgressIndicator()
            }

            is SolisClockDialogModel.Content -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier.weight(1f))
                    Text(
                        text = model.solisHours,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = model.solisSeconds,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

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

            SolisClockDialogModel.Error -> {
                Text(text = stringResource(id = R.string.solis_clock_error_message))
            }
        }
    }
}

class SolisClockDialogModelPreviewProvider : PreviewParameterProvider<SolisClockDialogModel> {
    override val values = sequenceOf(
        SolisClockDialogModel.Content(
            solisHours = "12 🌞 00",
            solisSeconds = "30",
            location = "Toronto, Canada",
            clock = SolisClockModel(
                dayStartAngle = -90f,
                dayEndAngle = 200f,
                needleAngle = 30f,
            )
        ),
        SolisClockDialogModel.Loading,
        SolisClockDialogModel.Error
    )
}

@PreviewLightDark
@Composable
private fun SolisClockWithTimePreview(
    @PreviewParameter(SolisClockDialogModelPreviewProvider::class) model: SolisClockDialogModel,
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
