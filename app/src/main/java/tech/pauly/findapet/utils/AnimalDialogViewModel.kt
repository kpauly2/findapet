@file:Suppress("UNUSED_PARAMETER")

package tech.pauly.findapet.utils

import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import io.reactivex.subjects.PublishSubject
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.AnimalUrl
import tech.pauly.findapet.shared.events.DialogEvent

class AnimalDialogViewModel(private val event: DialogEvent) {

    val primaryColor = ObservableInt(R.color.purpleStandard)
    val titleText = ObservableInt(R.string.unknown)
    val bodyText = ObservableString("")
    val positiveButtonText = ObservableInt(R.string.unknown)
    val negativeButtonText = ObservableInt(R.string.unknown)
    val imageUrl = ObservableField<AnimalUrl>("")

    val dismissSubject = PublishSubject.create<Unit>()

    init {
        primaryColor.set(event.primaryColor)
        titleText.set(event.titleText)
        bodyText.set(event.bodyText)
        positiveButtonText.set(event.positiveButtonText)
        negativeButtonText.set(event.negativeButtonText)
        imageUrl.set(event.imageUrl)
    }

    fun clickPositiveButton(v: View) {
        event.positiveButtonCallback?.invoke()
        dismiss()
    }

    fun clickNegativeButton(v: View) {
        event.negativeButtonCallback?.invoke()
        dismiss()
    }

    fun dismiss(v: View? = null) {
        dismissSubject.onNext(Unit)
    }
}

