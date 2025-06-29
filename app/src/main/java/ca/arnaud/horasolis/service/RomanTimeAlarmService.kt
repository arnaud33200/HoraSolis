package ca.arnaud.horasolis.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import ca.arnaud.horasolis.service.AlarmReceiver.Companion.ROMAN_TIME_ALARM_SERVICE_PARAM_EXTRA_KEY
import ca.arnaud.horasolis.domain.provider.TimeProvider
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class RomanTimeAlarmScheduleParam(
    val number: Int,
    val dateTime: LocalDateTime,
) : Parcelable {

    val requestCode: Int = number
}

class RomanTimeAlarmService(
    private val context: Context,
    private val timeProvider: TimeProvider,
) {

    fun scheduleAlarm(
        param: RomanTimeAlarmScheduleParam,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(param.requestCode, param)
        val zoneId = timeProvider.getZoneId()
        val timeInMillis = param.dateTime.atZone(zoneId).toInstant().toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    fun cancelAll() {
        for (requestCode in 1 until 24) {
            cancelAlarm(requestCode)
        }
    }

    fun cancelAlarm(requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(requestCode)
        alarmManager.cancel(pendingIntent)
    }

    /**
     * @param requestCode The request code for the alarm.
     * @param param Optional parameter to pass additional data to the receiver.
     *  null when cancelling an alarm, required when scheduling.
     */
    private fun createPendingIntent(
        requestCode: Int,
        param: RomanTimeAlarmScheduleParam? = null,
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            if (param != null) {
                putExtra(ROMAN_TIME_ALARM_SERVICE_PARAM_EXTRA_KEY, param)
            }
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
