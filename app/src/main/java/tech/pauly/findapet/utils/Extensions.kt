package tech.pauly.findapet.utils

import android.databinding.ObservableField
import android.view.View
import android.view.animation.Animation
import android.widget.ToggleButton

typealias ObservableString = ObservableField<String>
fun ObservableString.safeGet(): String {
    return this.get() ?: ""
}

val View.isToggleChecked: Boolean
    get() = (this as? ToggleButton)?.isChecked == true

fun View.runAnimation(setup: (View) -> Animation) {
    clearAnimation()
    animation = setup(this)
    this.animate()
}

fun Animation.addAnimationListener(animationRepeat: ((Animation?) -> Unit)? = null,
                                   animationStart: ((Animation?) -> Unit)? = null,
                                   animationEnd: ((Animation?) -> Unit)? = null): Animation {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            animationRepeat?.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            animationStart?.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            animationEnd?.invoke(animation)
        }
    })
    return this
}

sealed class Optional<out T> {
    data class Some<out T>(val element: T): Optional<T>()
    object None : Optional<Nothing>()
}