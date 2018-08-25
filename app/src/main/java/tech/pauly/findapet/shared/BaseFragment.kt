package tech.pauly.findapet.shared

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.PermissionEvent

abstract class BaseFragment : Fragment() {

    private val lifecycleSubscriptions = CompositeDisposable()
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PetApplication.component.permissionHelper()
    }

    override fun onResume() {
        subscribeToEventBus()
        super.onResume()
    }

    override fun onPause() {
        lifecycleSubscriptions.clear()
        super.onPause()
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

    fun permissionEvent(permissionEvent: PermissionEvent) {
        permissionHelper.requestPermission(activity!!, permissionEvent)
    }

    private fun subscribeToEventBus() {
        lifecycleSubscriptions.clear()
        viewEvents?.let { lifecycleSubscriptions.add(it) }
    }
}
