package ca.arnaud.horasolis.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import org.koin.androidx.compose.koinViewModel

sealed interface SolisClockDialogModel {

    data object Loading : SolisClockDialogModel
    data object Error : SolisClockDialogModel

    data class Content(
        val solisTime: String,
        val clock: SolisClockModel,
    ) : SolisClockDialogModel
}

@Composable
fun SolisClockDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    val viewModel: SolisClockViewModel = koinViewModel()
    val model by viewModel.state.collectAsState()

    SolisClockDialog(
        modifier = modifier,
        model = model,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun SolisClockDialog(
    modifier: Modifier = Modifier,
    model: SolisClockDialogModel,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onDismissRequest,
            ) {
                Text(stringResource(id = R.string.close_button))
            }
        },
        text = {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (val model = model) {
                    is SolisClockDialogModel.Loading -> {
                        CircularProgressIndicator()
                    }

                    is SolisClockDialogModel.Content -> {
                        Text(
                            text = model.solisTime,
                            style = MaterialTheme.typography.titleLarge,
                        )

                        SolisClock(
                            model = model.clock,
                            modifier = Modifier.size(300.dp)
                        )
                    }

                    SolisClockDialogModel.Error -> {
                        Text(text = "Failed to load Solis clock data")
                    }
                }
            }
        }
    )
}

class SolisClockDialogModelPreviewProvider : PreviewParameterProvider<SolisClockDialogModel> {
    override val values = sequenceOf(
        SolisClockDialogModel.Content(
            solisTime = "12:00 Day",
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
fun SolisClockDialogPreview(
    @PreviewParameter(SolisClockDialogModelPreviewProvider::class) model: SolisClockDialogModel
) {
    HoraSolisTheme {
        SolisClockDialog(
            modifier = Modifier.size(400.dp),
            model = model,
            onDismissRequest = {},
        )
    }
}
