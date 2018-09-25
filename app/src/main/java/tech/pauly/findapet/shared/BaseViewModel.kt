package tech.pauly.findapet.shared

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.BaseObservable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : BaseObservable(), BaseLifecycleViewModel  {

    private val lifecycleSubscriptions = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun disposeSubscriptions() {
        lifecycleSubscriptions.clear()
    }

    protected fun Disposable.onLifecycle() {
        lifecycleSubscriptions.add(this)
    }
}
