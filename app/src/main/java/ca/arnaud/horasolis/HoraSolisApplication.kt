package ca.arnaud.horasolis

import android.app.Application
import android.content.Context
import androidx.room.Room
import ca.arnaud.horasolis.data.HoraSolisDatabase
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.ScheduleTimesUseCase
import ca.arnaud.horasolis.domain.TimeProvider
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
        singleOf(::ScheduleTimesUseCase)
        singleOf(::RomanTimeAlarmService)
        workerOf(::ScheduleNextAlarmWorker)
        singleOf(::TimeProvider)
    }

    val databaseModule = module {
        single {
            Room.databaseBuilder(
                get(),
                HoraSolisDatabase::class.java,
                "horasolis.db"
            ).build()
        }
        single { get<HoraSolisDatabase>().selectedTimeDao() }
        single { get<HoraSolisDatabase>().scheduleSettingsDao() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(networkModule, appModule, databaseModule)
        }
    }
}