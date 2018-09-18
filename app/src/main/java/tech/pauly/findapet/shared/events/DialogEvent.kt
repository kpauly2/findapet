package tech.pauly.findapet.shared.events

import android.support.annotation.StringRes
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.AnimalUrl

data class DialogEvent(private val emitter: Any,
                       @StringRes val titleText: Int,
                       @StringRes val bodyText: String,
                       @StringRes val positiveButtonText: Int,
                       @StringRes val negativeButtonText: Int?,
                       val positiveButtonCallback: (() -> Unit)?,
                       val negativeButtonCallback: (() -> Unit)?,
                       val imageUrl: AnimalUrl?,
                       val primaryColor: Int = R.color.purpleStandard,
                       val blockBackgroundTouches: Boolean = false) : BaseViewEvent(emitter::class)