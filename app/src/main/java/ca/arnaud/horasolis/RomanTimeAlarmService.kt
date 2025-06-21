package ca.arnaud.horasolis

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import ca.arnaud.horasolis.AlarmReceiver.Companion.ROMAN_TIME_ALARM_SERVICE_PARAM_EXTRA_KEY
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

@Parcelize
data class RomanTimeAlarmScheduleParam(
    val number: Int,
    val dateTime: LocalDateTime,
) : Parcelable {

    val requestCode: Int = number
}

class RomanTimeAlarmService(
    private val context: Context,
) {

    fun scheduleAlarm(
        param: RomanTimeAlarmScheduleParam,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ROMAN_TIME_ALARM_SERVICE_PARAM_EXTRA_KEY, param)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            param.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = param.dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
