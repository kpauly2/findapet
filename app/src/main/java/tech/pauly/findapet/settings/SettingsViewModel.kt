package tech.pauly.findapet.settings

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import tech.pauly.findapet.R
import tech.pauly.findapet.data.SettingsEndpoints
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class SettingsViewModel @Inject
internal constructor(private val dataStore: TransientDataStore,
                     private val eventBus: ViewEventBus,
                     val adapter: SettingsAdapter) : BaseViewModel() {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbarTitle() {
        dataStore += DiscoverToolbarTitleUseCase(R.string.menu_settings)
        eventBus += OptionsMenuEvent(this, OptionsMenuState.EMPTY)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun populateAdapterItems() {
        adapter.viewModels = arrayListOf(
                SettingsTitleViewModel(R.string.about),
                SettingsLinkOutViewModel(R.string.app_name, SettingsEndpoints.sourceCodeEndpoint, eventBus),
                SettingsLinkOutViewModel(R.string.made_by, SettingsEndpoints.personalSiteEndpoint, eventBus))
    }
}
