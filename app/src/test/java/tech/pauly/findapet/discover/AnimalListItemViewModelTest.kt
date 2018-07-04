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
        on { id }.thenReturn(10)
        on { name }.thenReturn("name")
        on { age }.thenReturn(Age.ADULT)
        on { formattedBreedList }.thenReturn("breeds")
        on { primaryPhotoUrl }.thenReturn("photo.jpg")
    }
    private val eventBus: ViewEventBus = mock()
    private val dataStore: TransientDataStore = mock()
    private val resourceProvider: ResourceProvider = mock {
        on { getString(Age.ADULT.formattedName) }.thenReturn("Adult")
    }
    private val locationHelper: LocationHelper = mock()

    private val contact: Shelter = mock()
    private lateinit var subject: AnimalListItemViewModel

    @Before
    fun setup() {
        val media: Media = mock()
        whenever(media.photoList).thenReturn(emptyList())
        whenever(animal.contact).thenReturn(contact)
        whenever(locationHelper.getCurrentDistanceToContactInfo(contact)).thenReturn(Observable.just(1))
    }

    @Test
    fun onCreate_setAllBasicValuesForInputAnimal() {
        createSubject()

        subject.also {
            assertThat(it.id).isEqualTo(10)
            assertThat(it.name.get()).isEqualTo("name")
            assertThat(it.age.get()).isEqualTo("Adult")
            assertThat(it.breeds.get()).isEqualTo("breeds")
            assertThat(it.imageUrl.get()).isEqualTo("photo.jpg")
        }
    }

    @Test
    fun onCreate_getsDistanceForContactInfoAndDistanceLessThanZero_distanceVisibilityFalse() {
        whenever(locationHelper.getCurrentDistanceToContactInfo(contact)).thenReturn(Observable.just(-1))

        createSubject()

        assertThat(subject.distanceVisibility.get()).isFalse()
    }

    @Test
    fun launchAnimalDetails_launchesAnimalDetails() {
        createSubject()

        subject.launchAnimalDetails()

        verify(eventBus) += ActivityEvent(subject, AnimalDetailsActivity::class, false)
    }

    private fun createSubject() {
        subject = AnimalListItemViewModel(animal, eventBus, dataStore, resourceProvider)
    }
}