package tech.pauly.findapet.settings

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import tech.pauly.findapet.shared.isDebug

class SettingsViewModelTest {

    @RelaxedMockK
    private lateinit var dataStore: TransientDataStore

    @RelaxedMockK
    private lateinit var eventBus: ViewEventBus

    @MockK
    private lateinit var locationHelper: LocationHelper

    @RelaxedMockK
    private lateinit var adapter: SettingsAdapter

    private lateinit var subject: SettingsViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic("tech.pauly.findapet.shared.UtilsKt")
        every { isDebug() } returns false
        subject = SettingsViewModel(dataStore, eventBus, locationHelper, adapter)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbarTitle()

        verify {
            dataStore += DiscoverToolbarTitleUseCase(R.string.menu_settings)
            eventBus += OptionsMenuEvent(subject, OptionsMenuState.EMPTY)
        }
    }

    @Test
    fun populateAdapter_populatesAdapterWithItems() {
        subject.populateAdapterItems()

        verify {
            adapter.viewModels = arrayListOf(
                    SettingsTitleViewModel(R.string.about),
                    SettingsLinkOutViewModel(R.string.app_name, "https://github.com/kpauly2-ford/findapet", eventBus),
                    SettingsLinkOutViewModel(R.string.made_by, "https://www.pauly.tech", eventBus),
                    SettingsTitleViewModel(R.string.give_back),
                    SettingsEmailViewModel(R.string.feedback, eventBus),
                    SettingsTitleViewModel(R.string.references),
                    SettingsLinkOutViewModel(R.string.thanks, "https://kpauly2-ford.github.io/findapet/pages/attributions.html", eventBus),
                    SettingsLinkOutViewModel(R.string.licenses, "https://kpauly2-ford.github.io/findapet/pages/licenses.html", eventBus))
        }
    }
}