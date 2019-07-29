package tech.pauly.old.discover

import android.widget.ToggleButton
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.old.data.BreedRepository
import tech.pauly.old.data.FilterRepository
import tech.pauly.old.data.models.*
import tech.pauly.old.shared.datastore.FilterUpdatedUseCase
import tech.pauly.old.shared.datastore.TransientDataStore
import tech.pauly.old.shared.events.ActivityEvent
import tech.pauly.old.shared.events.ViewEventBus

class FilterViewModelTest {

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var breedRepository: BreedRepository

    @RelaxedMockK
    private lateinit var eventBus: ViewEventBus

    @RelaxedMockK
    private lateinit var dataStore: TransientDataStore

    @MockK
    private lateinit var filter: Filter

    @RelaxedMockK
    private lateinit var breedViewModel: BreedViewModel

    @MockK
    private lateinit var breedListResponse: BreedListResponse

    private lateinit var subject: FilterViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { filterRepository.currentFilter } returns Single.just(filter)
        every { filterRepository.insertFilter(any()) } returns Completable.complete()
        every { breedRepository.getBreedList(any()) } returns Single.just(breedListResponse)
        subject = FilterViewModel(filterRepository, eventBus, dataStore, breedViewModel)
    }

    @Test
    fun loadCurrentFilter_populatesScreenForFilter() {
        subject.apply {
            selectedSex.set(Sex.FEMALE)
            selectedAge.set(Age.YOUNG)
            selectedSize.set(AnimalSize.SMALL)
        }
        every { filter.sex } returns Sex.MALE
        every { filter.age } returns Age.ADULT
        every { filter.size } returns AnimalSize.LARGE
        every { filter.breed } returns "Calico"
        every { breedViewModel.selectedBreed } returns "Calico"

        subject.loadCurrentFilter()

        subject.apply {
            assertThat(selectedSex.get()).isEqualTo(Sex.MALE)
            assertThat(selectedAge.get()).isEqualTo(Age.ADULT)
            assertThat(selectedSize.get()).isEqualTo(AnimalSize.LARGE)
        }
        verify {
            breedViewModel.selectedBreed = "Calico"
        }
    }

    @Test
    fun checkSex_buttonNotChecked_setSexToU() {
        subject.selectedSex.set(Sex.FEMALE)
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns false

        subject.checkToggle(button, Sex.MALE)

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MISSING)
    }

    @Test
    fun checkSex_buttonChecked_setSexToButtonSex() {
        subject.selectedSex.set(Sex.FEMALE)
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns true

        subject.checkToggle(button, Sex.MALE)

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MALE)
    }

    @Test
    fun checkAge_buttonNotChecked_setAgeToMissing() {
        subject.selectedAge.set(Age.YOUNG)
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns false

        subject.checkToggle(button, Age.ADULT)

        assertThat(subject.selectedAge.get()).isEqualTo(Age.MISSING)
    }

    @Test
    fun checkAge_buttonChecked_setAgeToButtonAge() {
        subject.selectedAge.set(Age.YOUNG)
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns true

        subject.checkToggle(button, Age.ADULT)

        assertThat(subject.selectedAge.get()).isEqualTo(Age.ADULT)
    }

    @Test
    fun checkSize_buttonNotChecked_setSizeToMissing() {
        subject.selectedSize.set(AnimalSize.MEDIUM)
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns false

        subject.checkToggle(button, AnimalSize.SMALL)

        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.MISSING)
    }

    @Test
    fun checkSize_buttonChecked_setSizeToButtonSize() {
        subject.selectedSize.set(AnimalSize.MEDIUM)
        val button = mockk<ToggleButton>()
        every { button.isChecked } returns true

        subject.checkToggle(button, AnimalSize.SMALL)

        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.SMALL)
    }

    @Test
    fun saveFilter_finishesScreen() {
        every { breedViewModel.selectedBreed } returns "Calico"

        subject.saveFilter(mockk())

        verify {
            dataStore += FilterUpdatedUseCase()
            eventBus += ActivityEvent(subject, null, true)
        }
    }

    @Test
    fun saveFilter_insertsNewFilterForCurrentSelectedItems() {
        subject.apply {
            selectedSex.set(Sex.MALE)
            selectedAge.set(Age.ADULT)
            selectedSize.set(AnimalSize.LARGE)
        }
        every { breedViewModel.selectedBreed } returns "Calico"
        val slot = slot<Filter>()
        every { filterRepository.insertFilter(capture(slot)) } answers { Completable.never() }

        subject.saveFilter(mockk())

        slot.captured.apply {
            assertThat(sex).isEqualTo(Sex.MALE)
            assertThat(age).isEqualTo(Age.ADULT)
            assertThat(size).isEqualTo(AnimalSize.LARGE)
            assertThat(breed).isEqualTo("Calico")
        }
    }
}