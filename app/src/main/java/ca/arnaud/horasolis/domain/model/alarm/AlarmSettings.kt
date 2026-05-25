package ca.arnaud.horasolis.domain.model.alarm

/**
 * Global alarm settings that apply as defaults when an individual [Alarm] has not
 * overridden a particular preference (i.e. the per-alarm field is null).
 *
 * @property ringtoneUrl URI string for the default ringtone. null means the system default.
 * @property vibrate Whether alarms should vibrate by default.
 */
data class AlarmSettings(
    val ringtoneUrl: String?,
    val vibrate: Boolean,
)
