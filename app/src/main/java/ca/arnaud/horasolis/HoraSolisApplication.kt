package ca.arnaud.horasolis

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.data.ScheduleSettingsRepository
import ca.arnaud.horasolis.data.SolisRepository
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisCivilTimeUseCase
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.ScheduleNextDayAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.ScheduleSolisAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ClearAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.DeleteAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveSavedAlarmsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.location.GetCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.schedule.ObserveSelectedTimesUseCase
import ca.arnaud.horasolis.domain.usecase.schedule.SavedTimeScheduleUseCase
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.service.LocationService
import ca.arnaud.horasolis.service.SolisTimeAlarmService
import ca.arnaud.horasolis.ui.alarmmanager.AlarmListModelFactory
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerViewModel
import ca.arnaud.horasolis.ui.alarmmanager.EditLocationViewModel
import ca.arnaud.horasolis.ui.alarmmanager.EditSolisAlarmDialogModelFactory
import ca.arnaud.horasolis.ui.common.StringProvider
import ca.arnaud.horasolis.ui.main.HoraAlertDialogModelFactory
import ca.arnaud.horasolis.ui.main.MainViewModel
import ca.arnaud.horasolis.ui.timelist.TimeListScreenModelFactory
import ca.arnaud.horasolis.ui.timelist.TimeListViewModel
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

        // General
        singleOf(::StringProvider)

        // Service & Worker
        singleOf(::SolisTimeAlarmService)
        workerOf(::ScheduleNextAlarmWorker)
        factoryOf(::LocationService)

        // Main
        viewModelOf(::MainViewModel)
        factoryOf(::HoraAlertDialogModelFactory)

        // Time List
        viewModelOf(::TimeListViewModel)
        factoryOf(::TimeListScreenModelFactory)

        // Alarm Manager
        viewModelOf(::AlarmManagerViewModel)
        viewModelOf(::EditLocationViewModel)
        factoryOf(::AlarmListModelFactory)
        factoryOf(::EditSolisAlarmDialogModelFactory)
    }

    val domainModule = module {
        factoryOf(::GetSolisDayUseCase)
        factoryOf(::GetSolisCivilTimeUseCase)
        factoryOf(::SavedTimeScheduleUseCase)
        factoryOf(::ScheduleNextDayAlarmUseCase)
        factoryOf(::ScheduleSolisAlarmUseCase)
        factoryOf(::ObserveSelectedTimesUseCase)
        factoryOf(::SetAlarmRingingUseCase)
        factoryOf(::ClearAlarmRingingUseCase)
        factoryOf(::ObserveAlarmRingingUseCase)
        factoryOf(::ObserveSavedAlarmsUseCase)
        factoryOf(::UpsertAlarmUseCase)
        factoryOf(::DeleteAlarmUseCase)

        // location
        factoryOf(::GetCurrentLocationUseCase)
        factoryOf(::SetCurrentLocationUseCase)
        factoryOf(::ObserveLocationUseCase)

        singleOf(::TimeProvider)
    }

    val dataModule = module {
        singleOf(::SolisRepository)
        singleOf(::AlarmRepository)
        singleOf(::ScheduleSettingsRepository)
        singleOf(::LocationRepository)
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