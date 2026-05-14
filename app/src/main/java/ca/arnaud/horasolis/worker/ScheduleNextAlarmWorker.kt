package ca.arnaud.horasolis.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import ca.arnaud.horasolis.domain.flatMap
import ca.arnaud.horasolis.domain.usecase.alarm.ScheduleNextAlarmUseCase
import org.koin.java.KoinJavaComponent

data class ScheduleNextAlarmWorkerParam(
    val number: Int,
) {

    companion object {

        private const val NUMBER_DATA_KEY = "number"

        fun fromDataOrNull(data: Data): ScheduleNextAlarmWorkerParam? {
            val number = data.getInt(NUMBER_DATA_KEY, -1)
            return ScheduleNextAlarmWorkerParam(number)
        }
    }

    fun toData(): Data {
        return Data.Builder()
            .putInt(NUMBER_DATA_KEY, number)
            .build()
    }
}

class ScheduleNextAlarmWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val scheduleNextAlarm: ScheduleNextAlarmUseCase by lazy {
        KoinJavaComponent.get(ScheduleNextAlarmUseCase::class.java)
    }

    companion object {

        fun enqueue(
            context: Context,
            param: ScheduleNextAlarmWorkerParam,
        ) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<ScheduleNextAlarmWorker>()
                .setConstraints(constraints)
                .setInputData(param.toData())
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                uniqueWorkName = "ScheduleNextAlarmWorker_Time_${param.number}",
                existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                request = workRequest,
            )
        }
    }

    override suspend fun doWork(): Result {
        println("HORA_SOLIS_ALARM: worker job for number ${inputData.getInt("number", -1)}")
        val param = ScheduleNextAlarmWorkerParam.fromDataOrNull(inputData)
            ?: return Result.failure()

        return scheduleNextAlarm(alarmId = param.number).flatMap(
            transform = { Result.success() },
            transformError = { Result.failure() },
        )
    }
}
