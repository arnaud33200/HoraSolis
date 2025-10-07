package ca.arnaud.horasolis.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ca.arnaud.horasolis.R
import ca.arnaud.horasolis.domain.onFailure
import ca.arnaud.horasolis.domain.usecase.alarm.ClearAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingParams
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingUseCase
import ca.arnaud.horasolis.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class AlarmRingingService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_ID = "alarm_ringing_channel"
        private const val EXTRA_PARAMS = "alarm_ringing_params"
        private const val ACTION_STOP_ALARM = "STOP_ALARM"

        fun startService(
            context: Context,
            params: SolisTimeAlarmScheduleParam,
        ) {
            val serviceIntent = Intent(context, AlarmRingingService::class.java)
            serviceIntent.putExtra(EXTRA_PARAMS, params)
            context.startForegroundService(serviceIntent)
        }

        fun stopService(context: Context): Boolean {
            val serviceIntent = Intent(context, AlarmRingingService::class.java)
            return context.stopService(serviceIntent)
        }
    }

    private val setAlarmRinging: SetAlarmRingingUseCase by lazy {
        KoinJavaComponent.get(SetAlarmRingingUseCase::class.java)
    }

    private val clearAlarmRinging: ClearAlarmRingingUseCase by lazy {
        KoinJavaComponent.get(ClearAlarmRingingUseCase::class.java)
    }

    private var mediaPlayer: MediaPlayer? = null

    private val scope = CoroutineScope(context = Dispatchers.Default + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_ALARM) {
            stopSelf()
            return START_NOT_STICKY
        }

        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_PARAMS, SolisTimeAlarmScheduleParam::class.java)
        } else {
            intent?.getParcelableExtra(EXTRA_PARAMS)
        }

        if (params == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, createNotification())

        if (mediaPlayer?.isPlaying != true) {
            playAlarmSound(params)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        scope.launch { clearAlarmRinging() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun playAlarmSound(params: SolisTimeAlarmScheduleParam) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setLegacyStreamType(AudioManager.STREAM_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        requestAudioFocus(audioAttributes)
        playAlarmRingtone(audioAttributes)
        scope.launch {
            val alarmId = params.alarmId
            val setAlarmRingingParams = SetAlarmRingingParams(alarmId = alarmId)
            setAlarmRinging(setAlarmRingingParams).onFailure { error ->
                stopSelf()
            }
        }
    }

    private fun playAlarmRingtone(audioAttributes: AudioAttributes) {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(audioAttributes)
            setDataSource(applicationContext, alarmUri)
            isLooping = true
            setOnPreparedListener { start() }
            prepareAsync()
        }
    }

    private fun requestAudioFocus(audioAttributes: AudioAttributes) {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(audioAttributes)
            .build()
        audioManager.requestAudioFocus(request)
    }

    private fun createNotification(): Notification {
        val channelId = NOTIFICATION_CHANNEL_ID
        val channel = NotificationChannel(
            channelId,
            getString(R.string.alarm_ringing_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.alarm_ringing_notification_title))
            .setContentText(getString(R.string.alarm_ringing_notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(getMainActivityIntent())
            .addAction(
                R.drawable.ic_launcher_foreground,
                getString(R.string.ringing_alarm_dialog_button),
                getStopAlarmIntent()
            )
            .build()
    }

    private fun getStopAlarmIntent(): PendingIntent {
        val intent = Intent(this, AlarmRingingService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getService(this, 1001, intent, flags)
    }

    private fun getMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(this, 0, intent, flags)
    }
}