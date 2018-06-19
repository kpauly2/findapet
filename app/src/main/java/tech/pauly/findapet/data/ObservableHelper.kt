package tech.pauly.findapet.data

import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.SingleTransformer
import tech.pauly.findapet.dependencyinjection.IoScheduler
import tech.pauly.findapet.dependencyinjection.MainThreadScheduler
import javax.inject.Inject

open class ObservableHelper @Inject
constructor(@IoScheduler private val ioScheduler: Scheduler,
            @MainThreadScheduler private val mainThreadScheduler: Scheduler) {

    open fun <T> applyObservableSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(ioScheduler).observeOn(mainThreadScheduler)
        }
    }

    open fun <T> applySingleSchedulers(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.subscribeOn(ioScheduler).observeOn(mainThreadScheduler)
        }
    }
}
