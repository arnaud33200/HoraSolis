package ca.arnaud.horasolis

import android.app.Application
import org.koin.core.context.startKoin

class HoraSolisApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
        }
    }
}