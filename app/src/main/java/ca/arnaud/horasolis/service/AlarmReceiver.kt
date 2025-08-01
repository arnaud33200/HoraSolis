package ca.arnaud.horasolis.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ca.arnaud.horasolis.worker.ScheduleNextAlarmWorker
import ca.arnaud.horasolis.worker.ScheduleNextAlarmWorkerParam

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ROMAN_TIME_ALARM_SERVICE_PARAM_EXTRA_KEY = "alarm_service_param"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val param = intent.getParcelableExtra<SolisTimeAlarmScheduleParam>(
            ROMAN_TIME_ALARM_SERVICE_PARAM_EXTRA_KEY
        ) ?: return

        AlarmRingingService.startService(context, param)

        ScheduleNextAlarmWorker.Companion.enqueue(
            context = context,
            param = ScheduleNextAlarmWorkerParam(
                number = param.alarmId,
            )
        )
    }
}