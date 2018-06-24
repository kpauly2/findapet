package tech.pauly.findapet.discover

import android.widget.ToggleButton
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import tech.pauly.findapet.data.BreedRepository
import tech.pauly.findapet.data.FilterRepository
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import java.util.*

class FilterViewModelTest {

    private val filterRepository: FilterRepository = mock()
    private val breedRepository: BreedRepository = mock()
    private val eventBus: ViewEventBus = mock()
    private val dataStore: TransientDataStore = mock()
    private val filterAdapter: FilterAdapter = mock()
    private val filter: Filter = mock()

    private val breedListResponse: BreedListResponse = mock()
    private lateinit var subject: FilterViewModel

    @Before
    fun setup() {
        whenever(filterRepository.currentFilter).thenReturn(Single.just(filter))
        whenever(filterRepository.insertFilter(any())).thenReturn(Completable.complete())
        whenever(breedRepository.getBreedList(any())).thenReturn(Single.just(breedListResponse))
        subject = FilterViewModel(filterRepository, breedRepository, eventBus, dataStore, filterAdapter)
    }

    @Test
    fun create_setViewModelOnAdapter() {
        verify(filterAdapter).viewModel = subject
    }

    @Test
    fun getAdapter_returnsAdapter() {
        assertThat(subject.adapter).isEqualTo(filterAdapter)
    }

    @Test
    fun loadCurrentFilter_populatesScreenForFilter() {
        subject.apply {
            selectedSex.set(Sex.FEMALE)
            selectedAge.set(Age.YOUNG)
            selectedSize.set(AnimalSize.SMALL)
        }
        whenever(filter.sex).thenReturn(Sex.MALE)
        whenever(filter.age).thenReturn(Age.ADULT)
        whenever(filter.size).thenReturn(AnimalSize.LARGE)
        whenever(filter.breed).thenReturn("Calico")

        subject.loadCurrentFilter()

        subject.apply {
            assertThat(selectedSex.get()).isEqualTo(Sex.MALE)
            assertThat(selectedAge.get()).isEqualTo(Age.ADULT)
            assertThat(selectedSize.get()).isEqualTo(AnimalSize.LARGE)
            assertThat(selectedBreed.get()).isEqualTo("Calico")
        }
    }

    @Test
    fun checkSex_buttonNotChecked_setSexToU() {
        subject.selectedSex.set(Sex.FEMALE)
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(false)

        subject.checkSex(button, Sex.MALE)

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MISSING)
    }

    @Test
    fun checkSex_buttonChecked_setSexToButtonSex() {
        subject.selectedSex.set(Sex.FEMALE)
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(true)

        subject.checkSex(button, Sex.MALE)

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MALE)
    }

    @Test
    fun checkAge_buttonNotChecked_setAgeToMissing() {
        subject.selectedAge.set(Age.YOUNG)
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(false)

        subject.checkAge(button, Age.ADULT)

        assertThat(subject.selectedAge.get()).isEqualTo(Age.MISSING)
    }

    @Test
    fun checkAge_buttonChecked_setAgeToButtonAge() {
        subject.selectedAge.set(Age.YOUNG)
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(true)

        subject.checkAge(button, Age.ADULT)

        assertThat(subject.selectedAge.get()).isEqualTo(Age.ADULT)
    }

    @Test
    fun checkSize_buttonNotChecked_setSizeToMissing() {
        subject.selectedSize.set(AnimalSize.MEDIUM)
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(false)

        subject.checkSize(button, AnimalSize.SMALL)

        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.MISSING)
    }

    @Test
    fun checkSize_buttonChecked_setSizeToButtonSize() {
        subject.selectedSize.set(AnimalSize.MEDIUM)
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(true)

        subject.checkSize(button, AnimalSize.SMALL)

        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.SMALL)
    }

    @Test
    fun checkBreed_buttonNotChecked_setBreedToEmpty() {
        subject.selectedBreed.set("Calico")
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(false)

        subject.checkBreed(button, "Ragdoll")

        assertThat(subject.selectedBreed.get()).isEqualTo("")
    }

    @Test
    fun checkBreed_buttonChecked_setBreedToButtonBreed() {
        subject.selectedBreed.set("Calico")
        val button: ToggleButton = mock()
        whenever(button.isChecked).thenReturn(true)

        subject.checkBreed(button, "Ragdoll")

        assertThat(subject.selectedBreed.get()).isEqualTo("Ragdoll")
    }

    @Test
    fun saveFilter_finishesScreen() {
        subject.saveFilter(mock())

        verify(eventBus) += ActivityEvent(subject, null, true)
    }

    @Test
    fun saveFilter_insertsNewFilterForCurrentSelectedItems() {
        subject.apply {
            selectedSex.set(Sex.MALE)
            selectedAge.set(Age.ADULT)
            selectedSize.set(AnimalSize.LARGE)
            selectedBreed.set("Calico")
        }

        subject.saveFilter(mock())

        verify(filterRepository).insertFilter(check {
            assertThat(it.sex).isEqualTo(Sex.MALE)
            assertThat(it.age).isEqualTo(Age.ADULT)
            assertThat(it.size).isEqualTo(AnimalSize.LARGE)
            assertThat(it.breed).isEqualTo("Calico")
        })
    }

    @Test
    fun populateBreedList_noAnimalType_doNothing() {
        whenever(dataStore[FilterAnimalTypeUseCase::class]).thenReturn(null)

        subject.updateBreedList()

        verify(breedRepository, never()).getBreedList(any())
    }

    @Test
    fun populateBreedList_getsBreedListForAnimalType() {
        whenever(breedListResponse.breedList).thenReturn(null)
        setupDataStoreWithUseCase()

        subject.updateBreedList()

        verify(breedRepository).getBreedList(AnimalType.CAT)
    }

    @Test
    fun populateBreedList_returnedBreedListNull_doNothing() {
        whenever(breedListResponse.breedList).thenReturn(null)
        setupDataStoreWithUseCase()

        subject.updateBreedList()

        verify(filterAdapter, never()).setBreedItems(any())
    }

    @Test
    fun populateBreedList_returnedBreedListValid_showBreedList() {
        whenever(breedListResponse.breedList).thenReturn(Arrays.asList("breed 1", "breed 2"))
        setupDataStoreWithUseCase()

        subject.updateBreedList()

        verify(filterAdapter).setBreedItems(breedListResponse.breedList)
    }

    @Test
    fun populateBreedList_returnedBreedListValidAndHaveSelectedBreed_showBreedListWithSelectedBreedFirst() {
        subject.selectedBreed.set("breed 2")
        whenever(breedListResponse.breedList).thenReturn(Arrays.asList("breed 1", "breed 2"))
        setupDataStoreWithUseCase()

        subject.updateBreedList()

        verify(filterAdapter).setBreedItems(Arrays.asList("breed 2", "breed 1"))
    }


    @Test
    fun clickBreedSearch_fireScrollSubject() {
        val observer = subject.scrollToViewSubject.test()

        subject.clickBreedSearch()

        observer.assertValue(true)
    }

    @Test
    fun onBreedTextChanged_fireScrollSubject() {
        val observer = subject.scrollToViewSubject.test()

        subject.onBreedTextChanged("", 0, 0, 0)

        observer.assertValue(true)
    }

    @Test
    fun onBreedTextChanged_hasSelectedBreed_updateBreedItemsWithSelectedBreedFirst() {
        subject.selectedBreed.set("breed 2")
        whenever(breedListResponse.breedList).thenReturn(Arrays.asList("breed 1", "breed 2"))
        setupDataStoreWithUseCase()
        subject.updateBreedList()
        clearInvocations(filterAdapter)

        subject.onBreedTextChanged("breed", 0, 0, 0)

        verify(filterAdapter).setBreedItems(Arrays.asList("breed 2", "breed 1"))
    }

    @Test
    fun onBreedTextChanged_updateBreedItems() {
        whenever(breedListResponse.breedList).thenReturn(Arrays.asList("abc", "ab", "c"))
        setupDataStoreWithUseCase()
        subject.updateBreedList()

        subject.onBreedTextChanged("a", 0, 0, 0)

        verify(filterAdapter).setBreedItems(Arrays.asList("abc", "ab"))
    }

    @Test
    fun onBreedTextChanged_inputTextDifferentCapitals_updateBreedItems() {
        whenever(breedListResponse.breedList).thenReturn(Arrays.asList("abc", "ab", "c"))
        setupDataStoreWithUseCase()
        subject.updateBreedList()

        subject.onBreedTextChanged("A", 0, 0, 0)

        verify(filterAdapter).setBreedItems(Arrays.asList("abc", "ab"))
    }

    @Test
    fun onBreedTextChanged_noMatches_updateBreedItems() {
        whenever(breedListResponse.breedList).thenReturn(Arrays.asList("abc", "ab", "c"))
        setupDataStoreWithUseCase()
        subject.updateBreedList()

        subject.onBreedTextChanged("d", 0, 0, 0)

        verify(filterAdapter).setBreedItems(emptyList())
    }

    private fun setupDataStoreWithUseCase() {
        val useCase: FilterAnimalTypeUseCase = mock()
        whenever(useCase.animalType).thenReturn(AnimalType.CAT)
        whenever(dataStore[FilterAnimalTypeUseCase::class]).thenReturn(useCase)
    }
}