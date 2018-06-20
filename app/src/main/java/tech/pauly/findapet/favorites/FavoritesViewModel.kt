package tech.pauly.findapet.favorites

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import tech.pauly.findapet.R
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import javax.inject.Inject

class FavoritesViewModel @Inject
internal constructor(private val dataStore: TransientDataStore) : BaseViewModel() {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbarTitle() {
        dataStore += DiscoverToolbarTitleUseCase(R.string.menu_favorites)
    }
}
