package ca.arnaud.horasolis.domain.provider

import java.util.Locale
import java.util.TimeZone

interface LocaleProvider {
    fun getLocale(): Locale
    fun getTimezoneId(): String
}

class LocaleProviderImpl : LocaleProvider {
    override fun getLocale(): Locale = Locale.getDefault()
    override fun getTimezoneId(): String = TimeZone.getDefault().id
}
