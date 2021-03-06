package tech.pauly.findapet.settings

import android.content.Intent
import android.net.Uri
import android.view.View
import tech.pauly.findapet.data.SettingsEndpoints
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus

sealed class SettingsItemViewModel : BaseViewModel() {
    abstract var viewType: Int
}

open class SettingsTitleViewModel(val title: Int) : SettingsItemViewModel() {
    override var viewType = TITLE
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettingsTitleViewModel

        if (title != other.title) return false
        if (viewType != other.viewType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title
        result = 31 * result + viewType
        return result
    }

}

sealed class SettingsBasicViewModel(open val text: Int) : SettingsItemViewModel() {
    abstract fun onClick(v: View)
}

open class SettingsEmailViewModel(override val text: Int,
                                  private val eventBus: ViewEventBus) : SettingsBasicViewModel(text) {
    override var viewType = LINK_OUT

    override fun onClick(v: View) {
        eventBus += ActivityEvent(this,
                customIntent = Intent(Intent.ACTION_SENDTO).apply {
                    type = "text/plain"
                    data = Uri.parse("mailto:${SettingsEndpoints.personalEmail}")
                    flags += Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(Intent.EXTRA_SUBJECT, "Find a Pet feedback")
                })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettingsEmailViewModel

        if (text != other.text) return false
        if (eventBus != other.eventBus) return false
        if (viewType != other.viewType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text
        result = 31 * result + eventBus.hashCode()
        result = 31 * result + viewType
        return result
    }

}

open class SettingsLinkOutViewModel(override val text: Int,
                                    private val url: String,
                                    private val eventBus: ViewEventBus) : SettingsBasicViewModel(text) {
    override var viewType = LINK_OUT

    override fun onClick(v: View) {
        eventBus += ActivityEvent(this,
                customIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettingsLinkOutViewModel

        if (text != other.text) return false
        if (url != other.url) return false
        if (eventBus != other.eventBus) return false
        if (viewType != other.viewType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text
        result = 31 * result + url.hashCode()
        result = 31 * result + eventBus.hashCode()
        result = 31 * result + viewType
        return result
    }
}

open class SettingsCustomViewModel(override val text: Int,
                                   private val onClickListener: () -> Unit) : SettingsBasicViewModel(text) {

    override var viewType = CUSTOM

    override fun onClick(v: View) {
        onClickListener()
    }
}