package ca.arnaud.horasolis

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

data class MainScreenModel(
    val message: String = "",
)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    model: MainScreenModel,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = model.message
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    HoraSolisTheme {
        MainScreen(
            model = MainScreenModel(
                message = stringResource(id = R.string.app_name)
            )
        )
    }
}