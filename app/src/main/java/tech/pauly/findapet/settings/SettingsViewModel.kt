package tech.pauly.findapet.settings

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import tech.pauly.findapet.R
import tech.pauly.findapet.data.SettingsEndpoints
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import tech.pauly.findapet.shared.isDebug
import javax.inject.Inject

class SettingsViewModel @Inject
internal constructor(private val dataStore: TransientDataStore,
                     private val eventBus: ViewEventBus,
                     private val locationHelper: LocationHelper,
                     val adapter: SettingsAdapter) : BaseViewModel() {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbarTitle() {
        dataStore += DiscoverToolbarTitleUseCase(R.string.menu_settings)
        eventBus += OptionsMenuEvent(this, OptionsMenuState.EMPTY)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun populateAdapterItems() {
        val viewModels = arrayListOf(
                SettingsTitleViewModel(R.string.about),
                SettingsLinkOutViewModel(R.string.app_name, SettingsEndpoints.sourceCode, eventBus),
                SettingsLinkOutViewModel(R.string.made_by, SettingsEndpoints.personalSite, eventBus),
                SettingsTitleViewModel(R.string.give_back),
                SettingsEmailViewModel(R.string.feedback, eventBus),
                SettingsTitleViewModel(R.string.references),
                SettingsLinkOutViewModel(R.string.thanks, SettingsEndpoints.thanksAndReferences, eventBus),
                SettingsLinkOutViewModel(R.string.licenses, SettingsEndpoints.licenses, eventBus))
        if (isDebug()) {
            viewModels += arrayListOf(SettingsTitleViewModel(R.string.debug_settings),
                    SettingsCustomViewModel(R.string.hardcode_location) { locationHelper.debugLocation = true })
        }
        adapter.viewModels = viewModels
    }
}
