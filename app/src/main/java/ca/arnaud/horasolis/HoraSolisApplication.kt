package ca.arnaud.horasolis

import android.app.Application
import android.content.Context
import ca.arnaud.horasolis.data.HoraSolisDatabase
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.ObserveSelectedTimesUseCase
import ca.arnaud.horasolis.domain.SavedTimeScheduleUseCase
import ca.arnaud.horasolis.domain.ScheduleNextDayAlarmUseCase
import ca.arnaud.horasolis.domain.ScheduleRomanTimeUseCase
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
        singleOf(::RomanTimeAlarmService)
        workerOf(::ScheduleNextAlarmWorker)
        singleOf(::MainScreenModelFactory)
    }

    val domainModule = module {
        singleOf(::GetRomanTimesUseCase)
        singleOf(::SavedTimeScheduleUseCase)
        singleOf(::ScheduleNextDayAlarmUseCase)
        singleOf(::ScheduleRomanTimeUseCase)
        singleOf(::ObserveSelectedTimesUseCase)
        singleOf(::TimeProvider)
    }

    val databaseModule = module {
        single { HoraSolisDatabase.createDatabase(context = get()) }
        single { get<HoraSolisDatabase>().selectedTimeDao() }
        single { get<HoraSolisDatabase>().scheduleSettingsDao() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(networkModule, appModule, databaseModule, domainModule)
        }
    }
}