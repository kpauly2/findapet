package tech.pauly.findapet.shared

import android.support.v4.app.DialogFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseDialogFragment : DialogFragment() {

    private val lifecycleSubscriptions = CompositeDisposable()

    override fun onStop() {
        lifecycleSubscriptions.clear()
        super.onStop()
    }

    protected fun Disposable.onLifecycle() {
        lifecycleSubscriptions.add(this)
    }
}
