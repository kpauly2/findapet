package tech.pauly.findapet.shared

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.PermissionEvent
import tech.pauly.findapet.utils.getViewModel

abstract class BaseFragment : Fragment() {

    protected open val viewEvents: CompositeDisposable?
        get() = null

    protected val viewModelLifecycleObservers = ArrayList<BaseLifecycleViewModel>()
    private val lifecycleSubscriptions = CompositeDisposable()
    private var permissionHelper: PermissionHelper? = null

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PetApplication.component.permissionHelper()
    }

    override fun onStart() {
        subscribeToEventBus()
        super.onStart()
    }
    override fun onStop() {
        lifecycleSubscriptions.clear()
        super.onStop()
    }

    //endregion

    //region LifecycleObservers
    @Deprecated("use constructViewModel instead")
    protected fun addViewModelLifecycleObserver(viewModel: BaseLifecycleViewModel) {
        viewModelLifecycleObservers += viewModel
        lifecycle.addObserver(viewModel)
    }
    //endregion

    //region EventBus
    protected fun activityEvent(event: ActivityEvent) {
        event.customIntent?.let {
            startActivity(it)
            return
        }
        startActivity(Intent(context, event.startActivity?.java))
    }

    protected fun permissionEvent(permissionEvent: PermissionEvent) {
        activity ?: return
        permissionHelper?.requestPermission(activity!!, permissionEvent)
    }

    private fun subscribeToEventBus() {
        lifecycleSubscriptions.clear()
        viewEvents?.let { lifecycleSubscriptions.add(it) }
    }
    //endregion

    //region Extensions
    protected inline fun <reified T : BaseViewModel> constructViewModel(): T {
        return getViewModel<T>().also { viewModel: T ->
            viewModelLifecycleObservers += viewModel
            lifecycle.addObserver(viewModel)
        }
    }

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
