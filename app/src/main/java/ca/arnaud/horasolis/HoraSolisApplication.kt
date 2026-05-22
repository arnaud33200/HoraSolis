package ca.arnaud.horasolis

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import ca.arnaud.horasolis.data.AlarmRepository
import ca.arnaud.horasolis.data.LocationRepository
import ca.arnaud.horasolis.data.ScheduleRepository
import ca.arnaud.horasolis.data.SolisRepository
import ca.arnaud.horasolis.domain.model.location.LocationValidator
import ca.arnaud.horasolis.domain.provider.LocaleProvider
import ca.arnaud.horasolis.domain.provider.LocaleProviderImpl
import ca.arnaud.horasolis.domain.provider.TimeProvider
import ca.arnaud.horasolis.domain.usecase.GetSolisCivilTimeUseCase
import ca.arnaud.horasolis.domain.usecase.GetSolisDayUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ClearAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.DeleteAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.GetAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ObserveSavedAlarmsUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.ScheduleNextAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.SetAlarmRingingUseCase
import ca.arnaud.horasolis.domain.usecase.alarm.UpsertAlarmUseCase
import ca.arnaud.horasolis.domain.usecase.location.DeleteLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.GetCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.GetLocationOrNullUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveAllLocationsUseCase
import ca.arnaud.horasolis.domain.usecase.location.ObserveCurrentLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SaveLocationUseCase
import ca.arnaud.horasolis.domain.usecase.location.SetCurrentLocationUseCase
import ca.arnaud.horasolis.local.HoraSolisDatabase
import ca.arnaud.horasolis.remote.KtorClient
import ca.arnaud.horasolis.service.LocationService
import ca.arnaud.horasolis.service.SolisTimeAlarmService
import ca.arnaud.horasolis.ui.alarmmanager.AlarmListModelFactory
import ca.arnaud.horasolis.ui.alarmmanager.AlarmManagerViewModel
import ca.arnaud.horasolis.ui.alarmmanager.EditAlarmScreenModelFactory
import ca.arnaud.horasolis.ui.clock.SolisClockModelFactory
import ca.arnaud.horasolis.ui.clock.SolisClockViewModel
import ca.arnaud.horasolis.ui.clock.SolisClockWithTimeModelFactory
import ca.arnaud.horasolis.ui.common.DateFormatter
import ca.arnaud.horasolis.ui.common.DatePickerModelFactory
import ca.arnaud.horasolis.ui.common.StringProvider
import ca.arnaud.horasolis.ui.editalarm.EditAlarmViewModel
import ca.arnaud.horasolis.ui.editlocation.EditLocationViewModel
import ca.arnaud.horasolis.ui.editlocation.EditLocationViewModelParams
import ca.arnaud.horasolis.ui.locationmanager.LocationManagerScreenModelFactory
import ca.arnaud.horasolis.ui.locationmanager.LocationManagerViewModel
import ca.arnaud.horasolis.ui.main.HoraAlertDialogModelFactory
import ca.arnaud.horasolis.ui.main.MainViewModel
import ca.arnaud.horasolis.ui.onboarding.OnboardingViewModel
import ca.arnaud.horasolis.ui.solisviewer.SolisViewerScreenModelFactory
import ca.arnaud.horasolis.ui.solisviewer.SolisViewerViewModel
import ca.arnaud.horasolis.worker.ScheduleNextAlarmWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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
        singleOf(::DateFormatter)

        // Service & Worker
        singleOf(::SolisTimeAlarmService)
        workerOf(::ScheduleNextAlarmWorker)
        factoryOf(::LocationService)

        // Main
        viewModelOf(::MainViewModel)
        factoryOf(::HoraAlertDialogModelFactory)

        // Onboarding
        viewModelOf(::OnboardingViewModel)

        // Location Manager
        viewModelOf(::LocationManagerViewModel)
        factoryOf(::LocationManagerScreenModelFactory)
        singleOf(::LocationValidator)

        // Alarm Manager
        viewModelOf(::AlarmManagerViewModel)
        viewModel { params ->
            EditLocationViewModel(
                params = params.get<EditLocationViewModelParams>(),
                locationService = get(),
                saveLocation = get(),
                getLocationOrNull = get(),
                locationValidator = get(),
            )
        }
        viewModel { params ->
            EditAlarmViewModel(
                params = params.get(),
                getAlarm = get(),
                screenModelFactory = get(),
                upsertAlarm = get(),
                datePickerModelFactory = get(),
            )
        }
        factoryOf(::AlarmListModelFactory)
        factoryOf(::EditAlarmScreenModelFactory)
        factoryOf(::DatePickerModelFactory)

        // Clock
        factoryOf(::SolisClockModelFactory)
        factoryOf(::SolisClockWithTimeModelFactory)
        viewModelOf(::SolisClockViewModel)

        // Solis Viewer
        factoryOf(::SolisViewerScreenModelFactory)
        viewModelOf(::SolisViewerViewModel)
    }

    val domainModule = module {
        factoryOf(::GetSolisDayUseCase)
        factoryOf(::GetSolisCivilTimeUseCase)
        factoryOf(::ScheduleNextAlarmUseCase)
        factoryOf(::SetAlarmRingingUseCase)
        factoryOf(::ClearAlarmRingingUseCase)
        factoryOf(::ObserveAlarmRingingUseCase)
        factoryOf(::ObserveSavedAlarmsUseCase)
        factoryOf(::UpsertAlarmUseCase)
        factoryOf(::DeleteAlarmUseCase)

        factoryOf(::GetAlarmUseCase)

        // location
        factoryOf(::DeleteLocationUseCase)
        factoryOf(::GetCurrentLocationUseCase)
        factoryOf(::GetLocationOrNullUseCase)
        factoryOf(::SetCurrentLocationUseCase)
        factoryOf(::SaveLocationUseCase)
        factoryOf(::ObserveCurrentLocationUseCase)
        factoryOf(::ObserveAllLocationsUseCase)

        singleOf(::TimeProvider)
        singleOf(::LocaleProviderImpl) { bind<LocaleProvider>() }
    }

    val dataModule = module {
        singleOf(::SolisRepository)
        singleOf(::AlarmRepository)
        singleOf(::ScheduleRepository)
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