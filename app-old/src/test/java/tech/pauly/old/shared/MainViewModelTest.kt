package tech.pauly.old.shared

import android.widget.ToggleButton
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import tech.pauly.old.R
import tech.pauly.old.data.models.AnimalType
import tech.pauly.old.discover.DiscoverFragment
import tech.pauly.old.favorites.FavoritesFragment
import tech.pauly.old.settings.SettingsFragment
import tech.pauly.old.shared.datastore.DiscoverAnimalTypeUseCase
import tech.pauly.old.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.old.shared.datastore.TransientDataStore
import tech.pauly.old.shared.events.FragmentEvent
import tech.pauly.old.shared.events.ViewEventBus
import tech.pauly.old.shelters.SheltersFragment

class MainViewModelTest {

    private val eventBus: ViewEventBus = mock()
    private val dataStore: TransientDataStore = mock()
    private val useCase: DiscoverToolbarTitleUseCase = mock()

    private lateinit var subject: MainViewModel
    private val expandingLayoutObserver = TestObserver<MainViewModel.ExpandingLayoutEvent>()
    private val drawerCloseObserver = TestObserver<Boolean>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        whenever(useCase.title).thenReturn(R.string.tab_cat)
        whenever(dataStore.observeAndGetUseCase(DiscoverToolbarTitleUseCase::class)).thenReturn(Observable.just(useCase))
        subject = MainViewModel(eventBus, dataStore)
        subject.expandingLayoutSubject.subscribe(expandingLayoutObserver)
        subject.drawerSubject.subscribe(drawerCloseObserver)
    }

    @Test
    fun subscribeToDataStore_getToolbarTileUseCase_updateToolbarTitle() {
        subject.subscribeToDataStore()

        verify(dataStore).observeAndGetUseCase(DiscoverToolbarTitleUseCase::class)
        assertThat(subject.toolbarTitle.get()).isEqualTo(R.string.tab_cat)
    }

    @Test
    fun firstLaunch_launchesCatAnimalType() {
        subject.clickFirstAnimal()
        subject.subscribeToDataStore()

        verify(dataStore) += DiscoverAnimalTypeUseCase(AnimalType.CAT)
        verify(eventBus) += FragmentEvent(subject, DiscoverFragment::class, R.id.fragment_content)
    }

    @Test
    fun notFirstLaunch_doNotLaunchFragment() {
        subject.clickFirstAnimal()
        subject.subscribeToDataStore()
        clearInvocations(dataStore)
        clearInvocations(eventBus)

        subject.subscribeToDataStore()

        verify(dataStore, never()) += any()
        verify(eventBus, never()) += any()
    }

    @Test
    fun clickAnimalType_launchDiscoverFragmentForAnimalTypeAndSetsCurrentAnimalType() {
        subject.clickAnimalType(AnimalType.BARNYARD)

        assertThat(subject.currentAnimalType.get()).isEqualTo(AnimalType.BARNYARD)
        verify(dataStore) += DiscoverAnimalTypeUseCase(AnimalType.BARNYARD)
        verify(eventBus) += FragmentEvent(subject, DiscoverFragment::class, R.id.fragment_content)
        drawerCloseObserver.assertValue(true)
    }

    @Test
    fun clickStandardButton_buttonAlreadyChecked_stayChecked() {
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(false)

        subject.clickShelters(button)

        verify(button).isChecked = true
        drawerCloseObserver.assertValue(true)
        assertThat(subject.currentAnimalType.get()).isNull()
    }

    @Test
    fun clickStandardButton_buttonNotChecked_collapseDiscoverViewAndSetCurrentButtonAndResetCurrentAnimal() {
        val button = mockButton()

        subject.clickShelters(button)

        expandingLayoutObserver.assertValue(MainViewModel.ExpandingLayoutEvent.COLLAPSE)
        assertThat(subject.currentButton.get()).isEqualTo(1)
        verify(button, never()).isChecked = true
        drawerCloseObserver.assertValue(true)
        assertThat(subject.currentAnimalType.get()).isNull()
    }

    @Test
    fun clickShelters_launchesSheltersFragment() {
        subject.clickShelters(mockButton())

        verify(eventBus) += FragmentEvent(subject, SheltersFragment::class, R.id.fragment_content)
    }

    @Test
    fun clickFavoritesButton_launchesFavoritesFragment() {
        subject.clickFavorites(mockButton())

        verify(eventBus) += FragmentEvent(subject, FavoritesFragment::class, R.id.fragment_content)
    }

    @Test
    fun clickSettingsButton_launchesSettingsFragment() {
        subject.clickSettings(mockButton())

        verify(eventBus) += FragmentEvent(subject, SettingsFragment::class, R.id.fragment_content)
    }

    private fun mockButton(): ToggleButton {
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(true)
        whenever(button.id).thenReturn(1)
        return button
    }
}