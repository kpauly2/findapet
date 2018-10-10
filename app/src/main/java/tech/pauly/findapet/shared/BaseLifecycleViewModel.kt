package tech.pauly.findapet.shared

import android.arch.lifecycle.LifecycleObserver

interface BaseLifecycleViewModel : LifecycleObserver {
    fun onBackPressed(): Boolean { return true }
}