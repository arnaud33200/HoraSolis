package ca.arnaud.horasolis

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetRomanTimesUseCase
import ca.arnaud.horasolis.domain.usecase.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.ObserveSelectedTimesUseCase
import ca.arnaud.horasolis.domain.usecase.SavedTimeScheduleUseCase
import ca.arnaud.horasolis.domain.usecase.ScheduleNextDayAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.ScheduleRomanTimeUseCase
import ca.arnaud.horasolis.domain.usecase.SetAlarmRingingUseCase
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.data.ScheduleSettingsRepository
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.worker.ScheduleNextAlarmWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
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
        singleOf(::StringProvider)
        workerOf(::ScheduleNextAlarmWorker)
        factoryOf(::MainScreenModelFactory)
    }

    val domainModule = module {
        factoryOf(::GetRomanTimesUseCase)
        factoryOf(::SavedTimeScheduleUseCase)
        factoryOf(::ScheduleNextDayAlarmUseCase)
        factoryOf(::ScheduleRomanTimeUseCase)
        factoryOf(::ObserveSelectedTimesUseCase)
        factoryOf(::SetAlarmRingingUseCase)
        factoryOf(::ObserveAlarmRingingUseCase)
        singleOf(::TimeProvider)
    }

    val dataModule = module {
        singleOf(::AlarmRepository)
        singleOf(::ScheduleSettingsRepository)
        single { get<Context>().userDataStore }
    }

    val localModule = module {
        single { HoraSolisDatabase.createDatabase(context = get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(networkModule, appModule, localModule, domainModule, dataModule)
        }
    }
}

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "com.arnaud.horasolis.datastore.preferences"
)