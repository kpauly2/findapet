package tech.pauly.old.shared

import androidx.databinding.BaseObservable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : BaseObservable(), BaseLifecycleViewModel  {

    private val lifecycleSubscriptions = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun disposeSubscriptions() {
        lifecycleSubscriptions.clear()
    }

    //region Extensions
    protected fun Disposable.onLifecycle() {
        lifecycleSubscriptions.add(this)
    }

    protected inline fun <T> Observable<T>.quickSubscribe(crossinline onNext: (T) -> Unit) {
        this.subscribe({ onNext(it) }, Throwable::printStackTrace)
                .onLifecycle()
    }

    protected inline fun <T> Single<T>.quickSubscribe(crossinline onNext: (T) -> Unit) {
        this.subscribe({ onNext(it) }, Throwable::printStackTrace)
                .onLifecycle()
    }

    protected inline fun Completable.quickSubscribe(crossinline onComplete: () -> Unit) {
        this.subscribe({ onComplete() }, Throwable::printStackTrace)
                .onLifecycle()
    }
    //endregion
}
