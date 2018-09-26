package tech.pauly.findapet.shared

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.PermissionEvent

abstract class BaseFragment : Fragment() {

    private val viewModelLifecycleObservers = ArrayList<BaseLifecycleViewModel>()

    private val lifecycleSubscriptions = CompositeDisposable()
    private lateinit var permissionHelper: PermissionHelper

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

    protected open val viewEvents: CompositeDisposable?
        get() = null

    protected fun activityEvent(event: ActivityEvent) {
        event.customIntent?.let {
            startActivity(it)
            return
        }
        startActivity(Intent(context, event.startActivity?.java))
    }

    protected fun addViewModelLifecycleObserver(viewModel: BaseLifecycleViewModel) {
        viewModelLifecycleObservers += viewModel
        lifecycle.addObserver(viewModel)
    }

    protected fun permissionEvent(permissionEvent: PermissionEvent) {
        permissionHelper.requestPermission(activity!!, permissionEvent)
    }

    protected fun Disposable.onLifecycle() {
        lifecycleSubscriptions.add(this)
    }

    private fun subscribeToEventBus() {
        lifecycleSubscriptions.clear()
        viewEvents?.let { lifecycleSubscriptions.add(it) }
    }
}
