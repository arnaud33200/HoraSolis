package ca.arnaud.horasolis.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

@Composable
fun HoraTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        state = state,
        modifier = modifier,
        decorator = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = state.text.toString(),
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = visualTransformation,
                label = { Text(label) },
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                    start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp
                ),
                interactionSource = interactionSource,
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun HoraTextFieldPreview() {
    HoraSolisTheme {
        Surface {
            val state = TextFieldState(initialText = "48.858844")
            HoraTextField(
                state = state,
                label = "Latitude"
            )
        }
    }
}
