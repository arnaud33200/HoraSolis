package ca.arnaud.horasolis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ca.arnaud.horasolis.domain.SetAlarmRingingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class AlarmRingingService : Service() {

    companion object {

        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_ID = "alarm_ringing_channel"

        fun startService(context: Context) {
            val serviceIntent = Intent(context, AlarmRingingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }

        fun stopService(context: Context): Boolean {
            val serviceIntent = Intent(context, AlarmRingingService::class.java)
            return context.stopService(serviceIntent)
        }
    }

    private val setAlarmRinging: SetAlarmRingingUseCase by lazy {
        KoinJavaComponent.get(SetAlarmRingingUseCase::class.java)
    }

    private var mediaPlayer: MediaPlayer? = null

    private val scope = CoroutineScope(context = Dispatchers.Default + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("HORA_SOLIS_ALARM: ringing service started")
        startForeground(NOTIFICATION_ID, createNotification())
        if (mediaPlayer?.isPlaying != true) {
            playAlarmSound()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        scope.launch { setAlarmRinging(false) }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun playAlarmSound() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, alarmUri)
            isLooping = true
            setOnPreparedListener { start() }
            prepareAsync()
        }
        scope.launch { setAlarmRinging(true) }
    }

    private fun createNotification(): Notification {
        val channelId = NOTIFICATION_CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.alarm_ringing_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.alarm_ringing_notification_title))
            .setContentText(getString(R.string.alarm_ringing_notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(getMainActivityIntent())
            .build()
    }

    private fun getMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or (
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
                )
        return PendingIntent.getActivity(this, 0, intent, flags)
    }
}
