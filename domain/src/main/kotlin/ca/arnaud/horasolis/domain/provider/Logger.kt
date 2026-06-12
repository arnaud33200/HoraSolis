package ca.arnaud.horasolis.domain.provider

interface Logger {

    fun logNonFatal(throwable: Throwable)
}
