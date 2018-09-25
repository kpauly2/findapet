package tech.pauly.findapet.discover

import android.widget.ToggleButton
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.data.BreedRepository
import tech.pauly.findapet.data.models.AnimalType
import tech.pauly.findapet.data.models.BreedListResponse
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore

class BreedViewModelTest {

    @MockK
    private lateinit var breedRepository: BreedRepository

    @RelaxedMockK
    private lateinit var dataStore: TransientDataStore

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @RelaxedMockK
    private lateinit var adapter: BreedAdapter

    @RelaxedMockK
    private lateinit var breedListResponse: BreedListResponse

    private lateinit var subject: BreedViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { dataStore[FilterAnimalTypeUseCase::class] } returns null
        every { breedRepository.getBreedList(any()) } returns Single.just(breedListResponse)
        every { adapter.addBreeds(any()) } answers { nothing }
        subject = BreedViewModel(breedRepository, dataStore, resourceProvider, adapter)
    }

    @Test
    fun create_setViewModelOnAdapter() {
        verify {
            adapter.viewModel = subject
        }
    }

    @Test
    fun oBackPressed_notSearching_returnTrue() {
        assertThat(subject.onBackPressed()).isTrue()
    }

    @Test
    fun onBackPressed_searching_closeSearchAndReturnFalse() {
        subject.beginSearch(mockk())
        val observer = subject.openBreedSubject.test()

        assertThat(subject.onBackPressed()).isFalse()
        observer.assertValue(false)
    }

    @Test
    fun selectBreed_buttonNotChecked_setBreedToEmptyAndResetListAndCloseSearch() {
        subject.selectedBreed = "Calico"
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns false
        val observer = subject.openBreedSubject.test()

        subject.selectBreed(button, "Ragdoll")

        assertThat(subject.selectedBreed).isEqualTo("")
        observer.assertValue(false)
        verify { adapter.addBreeds(any()) }
    }

    @Test
    fun selectBreed_buttonChecked_setBreedToButtonBreedAndResetListAndCloseSearch() {
        subject.selectedBreed = "Calico"
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns true
        val observer = subject.openBreedSubject.test()

        subject.selectBreed(button, "Ragdoll")

        assertThat(subject.selectedBreed).isEqualTo("Ragdoll")
        observer.assertValue(false)
        verify { adapter.addBreeds(any()) }
    }

    @Test
    fun beginSearch_noAnimalType_doNothing() {
        every { dataStore[FilterAnimalTypeUseCase::class] } returns null

        subject.beginSearch(mockk())

        verify(exactly = 0) { breedRepository.getBreedList(any()) }
    }

    @Test
    fun beginSearch_getsBreedListForAnimalType() {
        every { breedRepository.getBreedList(any()) } returns Single.never()
        setupDataStoreWithUseCase()
        val observer = subject.openBreedSubject.test()

        subject.beginSearch(mockk())

        observer.assertValue(true)
        verify { breedRepository.getBreedList(AnimalType.CAT) }
    }

    @Test
    fun beginSearch_returnedBreedListNull_doNothing() {
        every { breedListResponse.breedList } returns null
        setupDataStoreWithUseCase()

        subject.beginSearch(mockk())

       verify(exactly = 0) { adapter.addBreeds(any()) }
    }

    @Test
    fun beginSearch_returnedBreedListValid_showBreedList() {
        every { breedListResponse.breedList } returns listOf("breed 1", "breed 2")
        setupDataStoreWithUseCase()

        subject.beginSearch(mockk())

        verify { adapter.addBreeds(listOf("breed 1", "breed 2")) }
    }

    @Test
    fun beginSearch_returnedBreedListValidAndHaveSelectedBreed_showBreedListWithSelectedBreedFirst() {
        subject.selectedBreed = "breed 2"
        every { breedListResponse.breedList } returns listOf("breed 1", "breed 2")
        setupDataStoreWithUseCase()

        subject.beginSearch(mockk())

        verify { adapter.addBreeds(listOf("breed 2", "breed 1")) }
    }

    @Test
    fun onBreedTextChanged_hasSelectedBreed_updateBreedItemsWithSelectedBreedFirst() {
        subject.selectedBreed = "breed 2"
        every { breedListResponse.breedList } returns listOf("breed 1", "breed 2")
        setupDataStoreWithUseCase()
        subject.beginSearch(mockk())

        subject.onBreedTextChanged("breed", 0, 0, 0)

        verify { adapter.addBreeds(listOf("breed 2", "breed 1")) }
    }

    @Test
    fun onBreedTextChanged_updateBreedItems() {
        every { breedListResponse.breedList } returns listOf("abc", "ab", "c")
        setupDataStoreWithUseCase()
        subject.beginSearch(mockk())

        subject.onBreedTextChanged("a", 0, 0, 0)

        verify { adapter.addBreeds(listOf("abc", "ab")) }
    }

    @Test
    fun onBreedTextChanged_inputTextDifferentCapitals_updateBreedItems() {
        every { breedListResponse.breedList } returns listOf("abc", "ab", "c")
        setupDataStoreWithUseCase()
        subject.beginSearch(mockk())

        subject.onBreedTextChanged("A", 0, 0, 0)

        verify { adapter.addBreeds(listOf("abc", "ab")) }
    }

    @Test
    fun onBreedTextChanged_noMatches_updateBreedItems() {
        every { breedListResponse.breedList } returns listOf("abc", "ab", "c")
        setupDataStoreWithUseCase()
        subject.beginSearch(mockk())

        subject.onBreedTextChanged("d", 0, 0, 0)

        verify { adapter.addBreeds(emptyList()) }
    }

    private fun setupDataStoreWithUseCase() {
        val useCase: FilterAnimalTypeUseCase = mockk()
        every { useCase.animalType } returns AnimalType.CAT
        every { dataStore[FilterAnimalTypeUseCase::class] } returns useCase
    }
}