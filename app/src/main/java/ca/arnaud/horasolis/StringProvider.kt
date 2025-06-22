package ca.arnaud.horasolis

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(
    private val context: Context,
) {

    fun getString(
        @StringRes id: Int,
        vararg arguments: String = emptyArray()
    ): String {
        return context.getString(id, *arguments)
    }
}