package ca.arnaud.horasolis.provider

import ca.arnaud.horasolis.domain.provider.Logger
import com.google.firebase.crashlytics.FirebaseCrashlytics

class LoggerImpl : Logger {
    override fun logNonFatal(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}
