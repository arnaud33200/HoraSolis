package ca.arnaud.horasolis

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
        // TODO: Start the alarm ringing UI/service here

        val param = intent.getParcelableExtra<RomanTimeAlarmScheduleParam>(
            ROMAN_TIME_ALARM_SERVICE_PARAM_EXTRA_KEY
        ) ?: return

        ScheduleNextAlarmWorker.enqueue(
            context = context,
            param = ScheduleNextAlarmWorkerParam(
                number = param.number,
            )
        )
    }
}

