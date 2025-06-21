package ca.arnaud.horasolis

import android.app.Application
import android.content.Context
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.worker.ScheduleNextAlarmWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class HoraSolisApplication : Application() {

    val networkModule = module {
        singleOf(::KtorClient)
    }

    val appModule = module {
        single<Context> { applicationContext }
        viewModelOf(::MainViewModel)
        singleOf(::GetRomanTimesUseCase)
        singleOf(::RomanTimeAlarmService)
        workerOf(::ScheduleNextAlarmWorker)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(networkModule, appModule)
        }
    }
}