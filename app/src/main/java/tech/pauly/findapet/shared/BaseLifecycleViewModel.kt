package tech.pauly.findapet.shared

import androidx.lifecycle.LifecycleObserver

interface BaseLifecycleViewModel : LifecycleObserver {
    fun onBackPressed(): Boolean { return true }
}