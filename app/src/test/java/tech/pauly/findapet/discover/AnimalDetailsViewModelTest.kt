package tech.pauly.findapet.discover

import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.*
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import java.util.*

class AnimalDetailsViewModelTest {

    private val dataStore: TransientDataStore = mock()
    private val viewPagerAdapter: AnimalDetailsViewPagerAdapter = mock()
    private val imagesPagerAdapter: AnimalImagesPagerAdapter = mock()
    private val resourceProvider: ResourceProvider = mock()

    private lateinit var subject: AnimalDetailsViewModel

    @Test
    fun onCreate_getAnimalFromUseCase_setAllBasicValues() {
        createSubjectWithUseCase(setupFullAnimalUseCase())

        subject.apply {
            assertThat(name.get()).isEqualTo("name")
            assertThat(sex.get()).isEqualTo(R.string.male)
            assertThat(size.get()).isEqualTo(R.string.large)
            assertThat(age.get()).isEqualTo(R.string.adult)
        }
    }

    @Test
    fun onCreate_noUseCaseReceived_fieldsShowDefaultValues() {
        createSubjectWithUseCase(null)

        subject.apply {
            assertThat(name.get()).isEqualTo("")
            assertThat(age.get()).isEqualTo(R.string.missing)
            assertThat(breeds.get()).isEqualTo("")
            assertThat(sex.get()).isEqualTo(R.string.missing)
            assertThat(size.get()).isEqualTo(R.string.missing)
            assertThat(options.get()).isEqualTo("")
            assertThat(description.get()).isEqualTo("")
            assertThat(descriptionVisibility.get()).isEqualTo(false)
            assertThat(optionsVisibility.get()).isEqualTo(false)
        }
    }

    @Test
    fun onCreate_animalDescriptionFieldIsNotNull_setDescriptionAndVisibility() {
        createSubjectWithUseCase(setupFullAnimalUseCase())

        assertThat(subject.descriptionVisibility.get()).isEqualTo(true)
        assertThat(subject.description.get()).isEqualTo("description")
    }

    @Test
    fun onCreate_animalDescriptionFieldIsEmpty_setDescriptionAndVisibility() {
        val useCase = setupFullAnimalUseCase()
        whenever(useCase.animal.description).thenReturn("")

        createSubjectWithUseCase(useCase)

        assertThat(subject.descriptionVisibility.get()).isEqualTo(false)
    }

    @Test
    fun onCreate_animalPhotoPresent_addsImagesToPagerAdapter() {
        createSubjectWithUseCase(setupFullAnimalUseCase())

        verify(imagesPagerAdapter).setAnimalImages(check {
            assertThat(it).hasSize(1)
            assertThat(it[0].imageUrl.get()).isEqualTo("http://url.com")
        })
        assertThat(subject.imagesCount.get()).isEqualTo(1)
    }

    @Test
    fun onCreate_largeAnimalPhotoNotPresent_addsNoImagesToAdapter() {
        val useCase = setupFullAnimalUseCase()
        whenever(useCase.animal.media).thenReturn(null)
        createSubjectWithUseCase(useCase)

        verify(imagesPagerAdapter, never()).setAnimalImages(anyList())
        assertThat(subject.imagesCount.get()).isEqualTo(0)
    }

    @Test
    fun onCreate_animalHasNoOptions_optionsNotVisible() {
        val useCase = setupFullAnimalUseCase()
        whenever(useCase.animal.options).thenReturn(emptyList())

        createSubjectWithUseCase(useCase)

        assertThat(subject.optionsVisibility.get()).isEqualTo(false)
    }

    @Test
    fun onCreate_animalHasOptions_optionsListShown() {
        createSubjectWithUseCase(setupFullAnimalUseCase())

        assertThat(subject.optionsVisibility.get()).isEqualTo(true)
        assertThat(subject.options.get()).isEqualTo("Altered\nHouse Broken")
    }

    @Test
    fun imagePageChange_updateCurrentImagePosition() {
        createSubjectWithUseCase(setupFullAnimalUseCase())

        subject.imagePageChange(1)

        assertThat(subject.currentImagePosition.get()).isEqualTo(1)
    }

    private fun setupFullAnimalUseCase(): AnimalDetailsUseCase {
        val photo: Photo = mock {
            on { url }.thenReturn("http://url.com")
            on { size }.thenReturn(PhotoSize.LARGE)
        }

        val photo2: Photo = mock {
            on { url }.thenReturn("http://url2.com")
            on { size }.thenReturn(PhotoSize.PET_NOTE_THUMBNAIL)
        }

        val media: Media = mock {
            on { photoList }.thenReturn(Arrays.asList(photo, photo2))
        }

        val animal: Animal = mock {
            on { name }.thenReturn("name")
            on { sex }.thenReturn(Sex.MALE)
            on { size }.thenReturn(AnimalSize.LARGE)
            on { age }.thenReturn(Age.ADULT)
            on { description }.thenReturn("")
            on { this.media }.thenReturn(media)
            on { formattedBreedList }.thenReturn("breeds")
            on { options }.thenReturn(Arrays.asList(Option.ALTERED, Option.HOUSE_BROKEN))
            on { description }.thenReturn("description")
        }

        return mock {
            on { this.animal }.thenReturn(animal)
        }
    }

    private fun createSubjectWithUseCase(useCase: AnimalDetailsUseCase?) {
        whenever(dataStore[AnimalDetailsUseCase::class]).thenReturn(useCase)
        whenever(resourceProvider.getString(R.string.altered)).thenReturn("Altered")
        whenever(resourceProvider.getString(R.string.house_broken)).thenReturn("House Broken")
        whenever(resourceProvider.getString(Age.ADULT.formattedName)).thenReturn("Adult")
        subject = AnimalDetailsViewModel(dataStore, viewPagerAdapter, imagesPagerAdapter, resourceProvider)
    }
}