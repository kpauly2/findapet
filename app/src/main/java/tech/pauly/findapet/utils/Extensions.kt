package tech.pauly.findapet.utils

import android.databinding.ObservableField

typealias ObservableString = ObservableField<String>
fun ObservableString.safeGet(): String {
    return this.get() ?: ""
}
