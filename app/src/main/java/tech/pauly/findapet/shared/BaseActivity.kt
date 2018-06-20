package tech.pauly.findapet.shared

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.FragmentEvent
import tech.pauly.findapet.shared.events.PermissionEvent

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private val lifecycleSubscriptions = CompositeDisposable()
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PetApplication.component.permissionHelper()
    }

    public override fun onResume() {
        subscribeToEventBus()
        super.onResume()
    }

    public override fun onPause() {
        lifecycleSubscriptions.clear()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun fragmentEvent(event: FragmentEvent) {
        val newFragment = Fragment.instantiate(this, event.fragment.name)
        supportFragmentManager.beginTransaction()
                .replace(event.container, newFragment)
                .commit()
    }

    fun permissionEvent(permissionEvent: PermissionEvent) {
        permissionHelper.requestPermission(this, permissionEvent)
    }

    protected open fun registerViewEvents(): CompositeDisposable? {
        return null
    }

    protected fun activityEvent(event: ActivityEvent) {
        if (event.finishActivity) {
            finish()
        } else {
            startActivity(Intent(this, event.startActivity))
        }
    }

    protected fun subscribeOnLifecycle(subscription: Disposable) {
        lifecycleSubscriptions.add(subscription)
    }

    private fun subscribeToEventBus() {
        lifecycleSubscriptions.run {
            clear()
            registerViewEvents()?.let { add(it) }
        }
    }

    protected fun Disposable.onLifecycle() {
        lifecycleSubscriptions.add(this)
    }
}
