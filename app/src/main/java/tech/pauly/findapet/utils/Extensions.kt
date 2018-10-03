package tech.pauly.findapet.utils

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.view.View
import android.view.animation.Animation
import android.widget.ToggleButton
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.BaseViewModel

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

fun Context.getBitmapForDrawableId(@DrawableRes drawableId: Int): BitmapDescriptor {
    val drawable = this.getDrawable(drawableId)
    drawable ?: throw IllegalArgumentException("drawable $drawableId missing")
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

inline fun <reified T : BaseViewModel> Fragment.getViewModel(): T {
    return ViewModelProviders.of(this, PetApplication.component.viewModelFactory)[T::class.java]
}

sealed class Optional<out T> {
    data class Some<out T>(val element: T): Optional<T>()
    object None : Optional<Nothing>()
}