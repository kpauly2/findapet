package tech.pauly.findapet.shared

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import tech.pauly.findapet.R
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.events.*

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private val lifecycleSubscriptions = CompositeDisposable()
    private lateinit var permissionHelper: PermissionHelper

    protected var currentMenuState = OptionsMenuState.EMPTY

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

    protected fun fragmentEvent(event: FragmentEvent) {
        val newFragment = Fragment.instantiate(this, event.fragment.qualifiedName)
        supportFragmentManager.beginTransaction()
                .replace(event.container, newFragment)
                .commit()
    }

    protected fun permissionEvent(permissionEvent: PermissionEvent) {
        permissionHelper.requestPermission(this, permissionEvent)
    }


    protected fun optionsMenuEvent(event: OptionsMenuEvent) {
        currentMenuState = event.state
        invalidateOptionsMenu()
    }

    protected open fun registerViewEvents(): CompositeDisposable? {
        return null
    }

    protected fun activityEvent(event: ActivityEvent) {
        event.customIntent?.let {
            startActivity(it)
            return
        }
        if (event.finishActivity) {
            finish()
        } else if (event.startActivity != null) {
            startActivity(Intent(this, event.startActivity?.java))
        }
    }

    protected fun snackbarEvent(event: SnackbarEvent) {
        Snackbar.make(findViewById<View>(android.R.id.content), event.text, Snackbar.LENGTH_SHORT).also { snackbar ->
            snackbar.view.setBackgroundColor(getColor(R.color.purpleStandardDark))
            val textView = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            textView?.setTextColor(getColor(R.color.white))
            textView?.typeface = ResourcesCompat.getFont(this, R.font.quicksand_medium)
        }.show()
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
