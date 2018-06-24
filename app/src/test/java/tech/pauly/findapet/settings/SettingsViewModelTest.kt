package tech.pauly.findapet.settings

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

class SettingsViewModelTest {

    private val dataStore: TransientDataStore = mock()
    private val eventBus: ViewEventBus = mock()
    private val adapter: SettingsAdapter = mock()

    private lateinit var subject: SettingsViewModel

    @Before
    fun setup() {
        subject = SettingsViewModel(dataStore, eventBus, adapter)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbarTitle()

        verify(dataStore) += DiscoverToolbarTitleUseCase(R.string.menu_settings)
        verify(eventBus) += OptionsMenuEvent(subject, OptionsMenuState.EMPTY)
    }

    @Test
    fun populateAdapter_populatesAdapterWithItems() {
        subject.populateAdapterItems()

        verify(adapter).viewModels = arrayListOf(
                SettingsTitleViewModel(R.string.about),
                SettingsLinkOutViewModel(R.string.app_name, "https://github.com/kpauly2-ford/findapet", eventBus),
                SettingsLinkOutViewModel(R.string.made_by, "https://www.pauly.tech", eventBus))

    }
}