package tech.pauly.findapet

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import tech.pauly.findapet.di.CoroutineContextProvider
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(protected open val scope: CoroutineContextProvider) : ViewModel(),
    LifecycleObserver, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}