package tech.pauly.findapet.shelters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent

import javax.inject.Inject

import tech.pauly.findapet.R
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore

class SheltersViewModel @Inject
internal constructor(private val dataStore: TransientDataStore) : BaseViewModel() {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbarTitle() {
        dataStore.save(DiscoverToolbarTitleUseCase(R.string.menu_shelters))
    }
}
