package ca.arnaud.horasolis.ui.editalarm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable

/**
 * Result of the system ringtone picker.
 *
 * - [Data]: user confirmed a selection. [Data.uri] is the chosen ringtone URI string.
 * - [Cancelled]: user dismissed the picker without making a selection.
 * - [Error]: the picker returned an unexpected result code or malformed data.
 */
sealed interface RingtonePickerResult {
    data class Data(val uri: String) : RingtonePickerResult
    data object Cancelled : RingtonePickerResult
    data object Error : RingtonePickerResult
}

/**
 * [ActivityResultContract] that launches the system ringtone picker filtered to alarm sounds
 * and parses the result into a [RingtonePickerResult].
 *
 * Input: the currently selected URI string (or `null` for the system default), used to
 * pre-highlight the existing selection in the picker.
 *
 * Output: [RingtonePickerResult]
 */
class RingtonePickerActivityResultContract :
    ActivityResultContract<String?, RingtonePickerResult>() {

    override fun createIntent(context: Context, input: String?): Intent {
        val existingUri = input?.let { Uri.parse(it) }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        return Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingUri)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): RingtonePickerResult {
        if (resultCode == Activity.RESULT_CANCELED) return RingtonePickerResult.Cancelled
        // intent must be present; a missing intent after RESULT_OK is unexpected
        if (intent == null) return RingtonePickerResult.Error
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        } ?: return RingtonePickerResult.Error
        return RingtonePickerResult.Data(uri = uri.toString())
    }
}

@Composable
fun rememberRingtonePickerLauncher(
    onResult: (RingtonePickerResult) -> Unit,
): ActivityResultLauncher<String?> {
    return rememberLauncherForActivityResult(
        contract = RingtonePickerActivityResultContract(),
        onResult = onResult,
    )
}
