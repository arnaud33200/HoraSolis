package ca.arnaud.horasolis.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

enum class OnboardingUserAction {
    Retry,
    CurrentLocation,
    AddLocation,
}

sealed interface OnboardingScreenModel {

    data object Loading : OnboardingScreenModel
    data object Error : OnboardingScreenModel
    data class MissingLocation(
        val loading: Boolean = false,
    ) : OnboardingScreenModel
    data object Ready : OnboardingScreenModel
}

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    model: OnboardingScreenModel,
    onUserAction: (OnboardingUserAction) -> Unit,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .navigationBarsPadding()
            .fillMaxSize()
        when (model) {
            OnboardingScreenModel.Loading -> Box(
                modifier = contentModifier,
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            OnboardingScreenModel.Error -> ErrorContent(
                modifier = contentModifier,
                onRetryClick = { onUserAction(OnboardingUserAction.Retry) },
            )
            is OnboardingScreenModel.MissingLocation -> MissingLocationContent(
                modifier = contentModifier,
                loading = model.loading,
                onCurrentLocationClick = { onUserAction(OnboardingUserAction.CurrentLocation) },
                onAddLocationClick = { onUserAction(OnboardingUserAction.AddLocation) },
            )
            OnboardingScreenModel.Ready -> {}
        }
    }
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.onboarding_error_message),
            textAlign = TextAlign.Center,
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onRetryClick,
        ) {
            Text(text = stringResource(R.string.onboarding_retry_button))
        }
    }
}

@Composable
private fun MissingLocationContent(
    modifier: Modifier = Modifier,
    loading: Boolean,
    onCurrentLocationClick: () -> Unit,
    onAddLocationClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.onboarding_missing_location_message),
            textAlign = TextAlign.Center,
        )
        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            enabled = !loading,
            onClick = onCurrentLocationClick,
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(text = stringResource(R.string.onboarding_current_location_button))
            }
        }
        OutlinedButton(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            enabled = !loading,
            onClick = onAddLocationClick,
        ) {
            Text(text = stringResource(R.string.onboarding_add_location_button))
        }
    }
}

private class OnboardingScreenPreviewProvider : PreviewParameterProvider<OnboardingScreenModel> {
    override val values = sequenceOf(
        OnboardingScreenModel.Loading,
        OnboardingScreenModel.Error,
        OnboardingScreenModel.MissingLocation(),
        OnboardingScreenModel.MissingLocation(loading = true),
    )
}

@PreviewLightDark
@Composable
private fun OnboardingScreenPreview(
    @PreviewParameter(OnboardingScreenPreviewProvider::class) model: OnboardingScreenModel,
) {
    HoraSolisTheme {
        OnboardingScreen(
            model = model,
            onUserAction = {},
        )
    }
}
