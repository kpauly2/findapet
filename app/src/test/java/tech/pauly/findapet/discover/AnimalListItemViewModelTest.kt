package tech.pauly.findapet.discover

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus

class AnimalListItemViewModelTest {

    private val animal: Animal = mock {
        on { name }.thenReturn("name")
        on { age }.thenReturn(Age.ADULT)
        on { formattedBreedList }.thenReturn("breeds")
    }
    private val eventBus: ViewEventBus = mock()
    private val dataStore: TransientDataStore = mock()
    private val resourceProvider: ResourceProvider = mock {
        on { getString(Age.ADULT.formattedName) }.thenReturn("Adult")
    }
    private val locationHelper: LocationHelper = mock()

    private val contact: Contact = mock()
    private lateinit var subject: AnimalListItemViewModel

    @Before
    fun setup() {
        val media: Media = mock()
        whenever(media.photoList).thenReturn(emptyList())
        whenever(animal.media).thenReturn(media)
        whenever(animal.contact).thenReturn(contact)
        whenever(locationHelper.getCurrentDistanceToContactInfo(contact)).thenReturn(Observable.just(1))
    }

    @Test
    fun onCreate_setAllBasicValuesForInputAnimal() {
        createSubject()

        subject.also {
            assertThat(it.name.get()).isEqualTo("name")
            assertThat(it.age.get()).isEqualTo("Adult")
            assertThat(it.breeds.get()).isEqualTo("breeds")
        }
    }

    @Test
    fun onCreate_getsDistanceForContactInfo() {
        createSubject()

        verify(locationHelper).getCurrentDistanceToContactInfo(contact)
    }

    @Test
    fun onCreate_getsDistanceForContactInfoAndDistanceLessThanZero_distanceVisibilityFalse() {
        whenever(locationHelper.getCurrentDistanceToContactInfo(contact)).thenReturn(Observable.just(-1))

        createSubject()

        assertThat(subject.distanceVisibility.get()).isFalse()
    }

    @Test
    fun onCreate_getsDistanceForContactInfoAndDistanceZero_setsDistanceAndVisibility() {
        whenever(locationHelper.getCurrentDistanceToContactInfo(contact)).thenReturn(Observable.just(0))

        createSubject()

        assertThat(subject.distance.get()).isEqualTo("< 1")
        assertThat(subject.distanceVisibility.get()).isTrue()
    }

    @Test
    fun onCreate_getsDistanceForContactInfoAndDistanceMoreThanZero_setsDistanceAndVisibility() {
        whenever(locationHelper.getCurrentDistanceToContactInfo(contact)).thenReturn(Observable.just(2))

        createSubject()

        assertThat(subject.distance.get()).isEqualTo("2")
        assertThat(subject.distanceVisibility.get()).isTrue()
    }

    @Test
    fun setPhoto_animalHasXPhoto_setsPhoto() {
        val photo: Photo = mock {
            on { it.url }.thenReturn("http://url.com")
            on { it.size }.thenReturn(PhotoSize.LARGE)
        }
        val media: Media = mock()
        whenever(media.photoList).thenReturn(listOf(photo))
        whenever(animal.media).thenReturn(media)

        createSubject()

        assertThat(subject.imageUrl.get()).isEqualTo("http://url.com")
    }

    @Test
    fun setPhoto_animalHasNoPhotos_doNotSetPhoto() {
        val media: Media = mock()
        whenever(media.photoList).thenReturn(emptyList())
        whenever(animal.media).thenReturn(media)

        createSubject()

        assertThat(subject.imageUrl.get()).isEqualTo("")
    }

    @Test
    fun setPhoto_animalHasNullPhotos_doNotSetPhoto() {
        val media: Media = mock()
        whenever(media.photoList).thenReturn(null)
        whenever(animal.media).thenReturn(media)

        createSubject()

        assertThat(subject.imageUrl.get()).isEqualTo("")
    }

    @Test
    fun setPhoto_animalDoesNotHaveXPhoto_doNotSetPhoto() {
        val photo: Photo = mock {
            on { it.url }.thenReturn("http://url.com")
            on { it.size }.thenReturn(PhotoSize.FEATURED_PET_MODULE)
        }
        val media: Media = mock()
        whenever(media.photoList).thenReturn(listOf(photo))
        whenever(animal.media).thenReturn(media)

        createSubject()

        assertThat(subject.imageUrl.get()).isEqualTo("")
    }

    @Test
    fun setPhoto_mediaIsNull_doNotSetPhoto() {
        whenever(animal.media).thenReturn(null)

        createSubject()

        assertThat(subject.imageUrl.get()).isEqualTo("")
    }

    @Test
    fun launchAnimalDetails_launchesAnimalDetails() {
        createSubject()

        subject.launchAnimalDetails()

        verify(eventBus).send(ActivityEvent(subject, AnimalDetailsActivity::class.java, false))
    }

    private fun createSubject() {
        subject = AnimalListItemViewModel(animal, eventBus, dataStore, resourceProvider, locationHelper)
    }
}