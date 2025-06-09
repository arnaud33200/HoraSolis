package ca.arnaud.horasolis

import android.app.Application
import ca.arnaud.horasolis.domain.GetRomanTimesUseCase
import ca.arnaud.horasolis.remote.KtorClient
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class HoraSolisApplication : Application() {

    val networkModule = module {
        singleOf(::KtorClient)
    }



    val appModule = module {
        viewModelOf(::MainViewModel)
        singleOf(::GetRomanTimesUseCase)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(networkModule, appModule)
        }
    }
}