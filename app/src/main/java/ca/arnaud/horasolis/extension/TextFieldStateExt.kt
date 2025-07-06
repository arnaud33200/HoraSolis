package ca.arnaud.horasolis.extension

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText

/**
 * Replace the text in the TextFieldState, clearing it if the text is null or empty.
 */
fun TextFieldState.setText(text: String?) {
    if (text.isNullOrEmpty()) {
        clearText()
    } else {
        edit { replace(0, length, text) }
    }
}