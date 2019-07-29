package tech.pauly.old.shared.events

import androidx.annotation.StringRes
import tech.pauly.old.R
import tech.pauly.old.data.models.AnimalUrl

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