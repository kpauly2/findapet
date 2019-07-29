package tech.pauly.old.shared

import androidx.lifecycle.LifecycleObserver

interface BaseLifecycleViewModel : LifecycleObserver {
    fun onBackPressed(): Boolean { return true }
}