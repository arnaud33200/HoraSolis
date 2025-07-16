package ca.arnaud.horasolis.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
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
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = MaterialTheme.typography.bodyLarge
    val textColor = textStyle.color.takeOrElse {
        val focused = interactionSource.collectIsFocusedAsState().value
        colors.textColor(enabled, isError, focused)
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    CompositionLocalProvider(
        LocalTextSelectionColors provides colors.textSelectionColors
    ) {

        BasicTextField(
            state = state,
            modifier = modifier,
            textStyle = mergedTextStyle,
            decorator = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = state.text.toString(),
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    label = {
                        Text(text = label)
                    },
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                        )
                    }
                )
            }
        )
    }
}

@Stable
internal fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    focused: Boolean,
): Color = when {
    !enabled -> disabledTextColor
    isError -> errorTextColor
    focused -> focusedTextColor
    else -> unfocusedTextColor
}

@PreviewLightDark
@Composable
private fun HoraTextFieldPreview() {
    HoraSolisTheme {
        Surface {
            val state = TextFieldState(initialText = "48.858844")
            HoraTextField(
                modifier = Modifier.padding(12.dp),
                state = state,
                label = "Latitude"
            )
        }
    }
}
