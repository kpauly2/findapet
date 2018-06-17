package tech.pauly.findapet.shared

import java.util.*
import javax.inject.Inject

open class LocaleWrapper @Inject constructor() {
    open fun getLocale(): Locale {
        return Locale.getDefault()
    }
}