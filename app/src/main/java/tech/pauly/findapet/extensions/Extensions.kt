package tech.pauly.findapet.extensions

import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

inline fun <reified T> LiveData<T>.observe(owner: LifecycleOwner, crossinline block: (T) -> Unit) {
    observe(owner, Observer { block(it) })
}

fun RecyclerView.ViewHolder.getDrawable(@DrawableRes id: Int): Drawable? {
    return itemView.context.getDrawable(id)
}

fun RecyclerView.ViewHolder.getDimension(@DimenRes id: Int): Int? {
    return itemView.context.resources.getDimensionPixelSize(id)
}