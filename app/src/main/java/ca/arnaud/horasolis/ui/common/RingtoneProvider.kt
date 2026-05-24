package ca.arnaud.horasolis.ui.common

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri

class RingtoneProvider(
    private val context: Context,
) {

    /**
     * Returns the display name for the given ringtone URI string.
     * ringtone cannot be resolved.
     */
    fun getNameOrNull(uri: String?): String? {
        if (uri == null) return null
        return RingtoneManager.getRingtone(
            context, Uri.parse(uri)
        )?.getTitle(context)
    }
}
