package tech.pauly.findapet.favorites

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus

class FavoritesViewModelTest {

    private val dataStore: TransientDataStore = mock()
    private val eventBus: ViewEventBus = mock()

    private lateinit var subject: FavoritesViewModel

    @Before
    fun setup() {
        subject = FavoritesViewModel(dataStore, eventBus)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbar()

        verify(dataStore) += DiscoverToolbarTitleUseCase(R.string.menu_favorites)
        verify(eventBus) += OptionsMenuEvent(subject, OptionsMenuState.EMPTY)
    }
}