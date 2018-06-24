package tech.pauly.findapet.favorites

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import tech.pauly.findapet.R
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class FavoritesViewModel @Inject
internal constructor(private val dataStore: TransientDataStore,
                     private val eventBus: ViewEventBus) : BaseViewModel() {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbar() {
        dataStore += DiscoverToolbarTitleUseCase(R.string.menu_favorites)
        eventBus += OptionsMenuEvent(this, OptionsMenuState.EMPTY)
    }
}
