package ca.arnaud.horasolis.domain.provider

import java.util.Locale

interface LocaleProvider {
    fun getLocale(): Locale
}

class LocaleProviderImpl : LocaleProvider {
    override fun getLocale(): Locale = Locale.getDefault()
}
