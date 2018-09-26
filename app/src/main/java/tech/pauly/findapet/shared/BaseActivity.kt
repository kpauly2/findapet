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
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import tech.pauly.findapet.R
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.events.*
import tech.pauly.findapet.utils.AnimalDialogFragment

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected var currentMenuState = OptionsMenuState.EMPTY
    protected open val viewEvents: CompositeDisposable?
        get() = null

    private val lifecycleSubscriptions = CompositeDisposable()
    private val viewModelLifecycleObservers = ArrayList<BaseLifecycleViewModel>()
    private lateinit var permissionHelper: PermissionHelper

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //endregion

    //region LifecycleObservers
    override fun onBackPressed() {
        if (viewModelLifecycleObservers.map { it.onBackPressed() }.find { it } == true)
            return
        super.onBackPressed()
    }

    protected fun addViewModelLifecycleObserver(viewModel: BaseLifecycleViewModel) {
        viewModelLifecycleObservers += viewModel
        lifecycle.addObserver(viewModel)
    }
    //endregion

    //region EventBus
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

    protected fun activityEvent(event: ActivityEvent) {
        event.customIntent?.let {
            startActivity(it)
            return
        }

        if (event.finishActivity) {
            finish()
            return
        }

        event.startActivity?.let {
            startActivity(Intent(this, it.java))
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

    protected fun dialogEvent(event: DialogEvent) {
        AnimalDialogFragment().init(event).show(supportFragmentManager, "dialog")
    }

    private fun subscribeToEventBus() {
        lifecycleSubscriptions.run {
            clear()
            viewEvents?.let { add(it) }
        }
    }
    //endregion

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
