package tech.pauly.findapet.shared

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

data class LifecycleDisposable(val disposable: Disposable, val event: Lifecycle.Event)

class LifecycleDisposeBag(lifecycle: Lifecycle) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    val disposables = ArrayList<LifecycleDisposable>()

    fun add(event: Lifecycle.Event, disposable: Disposable) {
        disposables.add(LifecycleDisposable(disposable, event))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() = onLifecycleEvent(Lifecycle.Event.ON_STOP)

    private fun onLifecycleEvent(event: Lifecycle.Event) {
        disposables.filter { (_, currEvent) -> currEvent == event }
                .forEach { (disposable, _) -> disposable.dispose() }
    }
}

fun Disposable.disposeOn(bag: LifecycleDisposeBag, event: Lifecycle.Event) {
    bag.add(event, this)
}

fun Disposable.disposeOnStop(bag: LifecycleDisposeBag) {
    disposeOn(bag, Lifecycle.Event.ON_STOP)
}

open class BaseViewModel : BaseObservableViewModel(), BaseLifecycleViewModel {

    private var _disposeBag: LifecycleDisposeBag? = null
    protected val disposeBag: LifecycleDisposeBag
        get() = _disposeBag ?: throw IllegalArgumentException("Dispose bag is null, make sure you've called observeLifecycle")

    override fun observeLifecycle(lifecycle: Lifecycle) {
        _disposeBag = LifecycleDisposeBag(lifecycle)
    }

    protected inline fun <T> Observable<T>.quickSubscribe(crossinline onNext: (T) -> Unit) {
        this.subscribe({ onNext(it) }, Throwable::printStackTrace)
                .disposeOnStop(disposeBag)
    }

    protected inline fun <T> Single<T>.quickSubscribe(crossinline onNext: (T) -> Unit) {
        this.subscribe({ onNext(it) }, Throwable::printStackTrace)
                .disposeOnStop(disposeBag)
    }

    protected inline fun Completable.quickSubscribe(crossinline onComplete: () -> Unit) {
        this.subscribe({ onComplete() }, Throwable::printStackTrace)
                .disposeOnStop(disposeBag)
    }
    //endregion
}
