package tech.pauly.findapet.utils

import android.databinding.ObservableField

typealias ObservableString = ObservableField<String>
fun ObservableString.safeGet(): String {
    return this.get() ?: ""
}

sealed class Optional<out T> {
    data class Some<out T>(val element: T): Optional<T>()
    object None : Optional<Nothing>()
}