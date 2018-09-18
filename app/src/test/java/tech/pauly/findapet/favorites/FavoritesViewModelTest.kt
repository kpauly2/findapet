package tech.pauly.findapet.favorites

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.AnimalRepository
import tech.pauly.findapet.data.FavoriteRepository
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.discover.AnimalListAdapter
import tech.pauly.findapet.discover.AnimalListItemViewModel
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.SentencePlacement
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.DialogEvent
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus

class FavoritesViewModelTest {

    @RelaxedMockK
    private lateinit var listAdapter: AnimalListAdapter

    @MockK
    private lateinit var animalListItemFactory: AnimalListItemViewModel.Factory

    @RelaxedMockK
    private lateinit var dataStore: TransientDataStore

    @RelaxedMockK
    private lateinit var eventBus: ViewEventBus

    @RelaxedMockK
    private lateinit var favoriteRepository: FavoriteRepository

    @MockK
    private lateinit var animalRepository: AnimalRepository

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @RelaxedMockK
    private lateinit var favorite1: LocalAnimal

    @RelaxedMockK
    private lateinit var favorite2: LocalAnimal

    @RelaxedMockK
    private lateinit var favorite3: LocalAnimal

    @MockK
    private lateinit var favoriteVm1: AnimalListItemViewModel

    @MockK
    private lateinit var favoriteVm2: AnimalListItemViewModel

    @MockK
    private lateinit var favoriteVm3: AnimalListItemViewModel

    private lateinit var subject: FavoritesViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { favorite1.id } returns 1
        every { favorite2.id } returns 2
        every { favorite3.id } returns 3
        every { favoriteRepository.getFavoritedAnimals() } returns Observable.just(listOf(favorite1, favorite2))

        subject = FavoritesViewModel(listAdapter, animalListItemFactory, dataStore, eventBus, favoriteRepository, animalRepository, resourceProvider)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbar()

        verify {
            dataStore += DiscoverToolbarTitleUseCase(R.string.menu_favorites)
            eventBus += OptionsMenuEvent(subject, OptionsMenuState.EMPTY)
        }
    }

    @Test
    fun loadFavorites_clearItemsSetsRefreshingAndGetFavoritedAnimals() {
        every { favoriteRepository.getFavoritedAnimals() } returns Observable.never()

        subject.loadFavorites()

        assertThat(subject.refreshing.get()).isTrue()
        verify {
            listAdapter.clearAnimalItems()
            favoriteRepository.getFavoritedAnimals()
        }
    }

    @Test
    fun loadFavorites_getsFavoritedAnimals_fetchAllAnimalsFromNetwork() {
        every { animalRepository.fetchAnimal(any()) } returns Observable.never()

        subject.loadFavorites()

        verify {
            animalRepository.fetchAnimal(favorite1)
            animalRepository.fetchAnimal(favorite2)
        }
    }

    @Test
    fun loadFavorites_getsFavoritedAnimalsAndFetchAllAnimalsFromNetworkWithStatusOk_showAllAnimals() {
        val favorite1Response = mockk<AnimalResponseWrapper> {
            every { header.status?.code } returns StatusCode.PFAPI_OK
            every { animal } returns favorite1
        }
        val favorite2Response = mockk<AnimalResponseWrapper> {
            every { header.status?.code } returns StatusCode.PFAPI_OK
            every { animal } returns favorite2
        }
        every { animalListItemFactory.newInstance(favorite1) } returns favoriteVm1
        every { animalListItemFactory.newInstance(favorite2) } returns favoriteVm2
        every { animalRepository.fetchAnimal(favorite1) } returns Observable.just(favorite1Response)
        every { animalRepository.fetchAnimal(favorite2) } returns Observable.just(favorite2Response)

        subject.loadFavorites()

        verify {
            listAdapter.addAnimalItem(favoriteVm1)
            listAdapter.addAnimalItem(favoriteVm2)
        }
    }

    @Test
    fun loadFavorites_fetchAnimalsFromNetworkWithWarningAnimals_showAllWarningsForUnauthorizedAnimals() {
        val warningHeader = mockk<Header> {
            every { this@mockk.status?.code } returns StatusCode.PFAPI_ERR_UNAUTHORIZED
        }
        val successHeader = mockk<Header> {
            every { this@mockk.status?.code } returns StatusCode.PFAPI_OK
        }
        every { animalListItemFactory.newInstance(favorite1) } returns favoriteVm1
        every { animalListItemFactory.newInstance(favorite2) } returns favoriteVm2
        every { animalListItemFactory.newInstance(favorite3) } returns favoriteVm3
        every { animalRepository.fetchAnimal(favorite1) } returns Observable.just(AnimalResponseWrapper(favorite1, warningHeader))
        every { animalRepository.fetchAnimal(favorite2) } returns Observable.just(AnimalResponseWrapper(favorite2, successHeader))
        every { animalRepository.fetchAnimal(favorite3) } returns Observable.just(AnimalResponseWrapper(favorite3, warningHeader))
        every { favoriteRepository.getFavoritedAnimals() } returns Observable.just(listOf(favorite1, favorite2, favorite3))

        subject.loadFavorites()

        verify {
            favorite1.warning = true
            favorite3.warning = true
            listAdapter.addAnimalItem(favoriteVm1)
            listAdapter.addAnimalItem(favoriteVm2)
            listAdapter.addAnimalItem(favoriteVm3)
        }
        verify(exactly = 0) {
            favorite2.warning = true
        }
    }

    @Test
    fun loadFavorites_fetchAnimalsFromNetworkWithAdoptedAnimal_showDialogForAdoptedAnimalAndDoNotShowInList() {
        val adoptedHeader = mockk<Header> {
            every { this@mockk.status?.code } returns StatusCode.PFAPI_ERR_NOENT
        }
        val successHeader = mockk<Header> {
            every { this@mockk.status?.code } returns StatusCode.PFAPI_OK
        }
        every { favorite1.sex } returns Sex.MALE
        every { favorite1.name } returns "Max"
        every { favorite1.primaryPhotoUrl } returns "photo.jpg"
        every { animalListItemFactory.newInstance(favorite1) } returns favoriteVm1
        every { animalListItemFactory.newInstance(favorite2) } returns favoriteVm2
        every { animalRepository.fetchAnimal(favorite1) } returns Observable.just(AnimalResponseWrapper(favorite1, adoptedHeader))
        every { animalRepository.fetchAnimal(favorite2) } returns Observable.just(AnimalResponseWrapper(favorite2, successHeader))
        every { resourceProvider.getSexString(R.string.pet_adopted_dialog_body, Sex.MALE, "Max", SentencePlacement.SUBJECT) } returns "body"
        val slot = slot<DialogEvent>()
        every { eventBus += capture(slot) } answers { nothing }

        subject.loadFavorites()

        slot.captured.apply {
            assertThat(titleText).isEqualTo(R.string.pet_adopted_dialog_title)
            assertThat(bodyText).isEqualTo("body")
            assertThat(positiveButtonText).isEqualTo(R.string.pet_adopted_remove_button)
            assertThat(negativeButtonText).isEqualTo(null)
            assertThat(imageUrl).isEqualTo("photo.jpg")
            assertThat(blockBackgroundTouches).isEqualTo(true)
        }
        verify {
            listAdapter.addAnimalItem(favoriteVm2)
        }
        verify(exactly = 0) {
            listAdapter.addAnimalItem(favoriteVm1)
        }
    }

    @Test
    fun loadFavorites_fetchAnimalsFromNetworkWithTwoAdoptedAnimal_showDialogForAdoptedAnimalsInOrderAndUnfavoriteWhenButtonPressed() {
        val adoptedHeader = mockk<Header> {
            every { this@mockk.status?.code } returns StatusCode.PFAPI_ERR_NOENT
        }
        every { favorite1.sex } returns Sex.MALE
        every { favorite1.name } returns "Max"
        every { favorite1.primaryPhotoUrl } returns "photo1.jpg"
        every { favorite2.sex } returns Sex.FEMALE
        every { favorite2.name } returns "Roxy"
        every { favorite2.primaryPhotoUrl } returns "photo2.jpg"
        every { animalListItemFactory.newInstance(favorite1) } returns favoriteVm1
        every { animalListItemFactory.newInstance(favorite2) } returns favoriteVm2
        every { animalRepository.fetchAnimal(favorite1) } returns Observable.just(AnimalResponseWrapper(favorite1, adoptedHeader))
        every { animalRepository.fetchAnimal(favorite2) } returns Observable.just(AnimalResponseWrapper(favorite2, adoptedHeader))
        every { resourceProvider.getSexString(R.string.pet_adopted_dialog_body, Sex.MALE, "Max", SentencePlacement.SUBJECT) } returns "body1"
        every { resourceProvider.getSexString(R.string.pet_adopted_dialog_body, Sex.FEMALE, "Roxy", SentencePlacement.SUBJECT) } returns "body2"
        val slot = slot<DialogEvent>()
        every { eventBus += capture(slot) } answers { nothing }

        subject.loadFavorites()

        assertThat(slot.captured.bodyText).isEqualTo("body1")

        slot.captured.positiveButtonCallback?.invoke()

        assertThat(slot.captured.bodyText).isEqualTo("body2")

        slot.captured.positiveButtonCallback?.invoke()

        verify {
            favoriteRepository.unfavoriteAnimal(favorite1)
            favoriteRepository.unfavoriteAnimal(favorite2)
        }
        verify(exactly = 0) {
            listAdapter.addAnimalItem(favoriteVm1)
            listAdapter.addAnimalItem(favoriteVm2)
        }
    }
}