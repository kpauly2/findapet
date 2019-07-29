package tech.pauly.old.discover

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.old.R
import tech.pauly.old.data.models.Age
import tech.pauly.old.data.models.Animal
import tech.pauly.old.data.models.Sex
import tech.pauly.old.data.models.Shelter
import tech.pauly.old.shared.LocationHelper
import tech.pauly.old.shared.ResourceProvider
import tech.pauly.old.shared.SentencePlacement
import tech.pauly.old.shared.datastore.AnimalDetailsUseCase
import tech.pauly.old.shared.datastore.TransientDataStore
import tech.pauly.old.shared.events.ActivityEvent
import tech.pauly.old.shared.events.DialogEvent
import tech.pauly.old.shared.events.ViewEventBus

class AnimalListItemViewModelTest {

    @MockK
    private lateinit var animal: Animal

    @RelaxedMockK
    private lateinit var eventBus: ViewEventBus

    @RelaxedMockK
    private lateinit var dataStore: TransientDataStore

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @MockK
    private lateinit var locationHelper: LocationHelper

    @MockK
    private lateinit var contact: Shelter

    @MockK

    private lateinit var subject: AnimalListItemViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        animal.apply {
            every { id } returns 10
            every { name } returns "name"
            every { age } returns Age.ADULT
            every { formattedBreedList } returns "breeds"
            every { primaryPhotoUrl } returns "photo.jpg"
            every { sex } returns Sex.MISSING
            every { warning } returns true
        }

        resourceProvider.apply {
            every { getString(Age.ADULT.formattedName) } returns "Adult"
            every { getString(R.string.pronoun_male_subject) } returns "he"
            every { getString(R.string.pronoun_female_subject) } returns "she"
            every { getString(R.string.pronoun_missing) } returns "it"
            every { getString(R.string.pronoun_male_object) } returns "him"
            every { getString(R.string.pronoun_female_object) } returns "her"
        }

        every { animal.contact } returns contact
        every { locationHelper.getCurrentDistanceToContactInfo(contact) } returns Observable.just(1)
    }

    @Test
    fun onCreate_setAllBasicValuesForInputAnimal() {
        createSubject()

        subject.apply {
            assertThat(id).isEqualTo(10)
            assertThat(name.get()).isEqualTo("name")
            assertThat(age.get()).isEqualTo("Adult")
            assertThat(breeds.get()).isEqualTo("breeds")
            assertThat(imageUrl.get()).isEqualTo("photo.jpg")
            assertThat(warning.get()).isTrue()
        }
    }

    @Test
    fun onCreate_getsDistanceForContactInfoAndDistanceLessThanZero_distanceVisibilityFalse() {
        every { locationHelper.getCurrentDistanceToContactInfo(contact) } returns Observable.just(-1)

        createSubject()

        assertThat(subject.distanceVisibility.get()).isFalse()
    }

    @Test
    fun launchAnimalDetails_launchesAnimalDetailsToDetailsTab() {
        createSubject()

        subject.launchAnimalDetails(AnimalDetailsUseCase.Tab.DETAILS)

        verify { dataStore += AnimalDetailsUseCase(animal, AnimalDetailsUseCase.Tab.DETAILS) }
        verify { eventBus += ActivityEvent(subject, AnimalDetailsActivity::class, false) }
    }

    @Test
    fun showPetWarningDialog_noWarning_launchAnimalDetailsToDetailsTab() {
        createSubject()
        subject = spyk(subject)
        subject.warning.set(false)

        subject.showPetWarningDialog(mockk())

        verify { subject.launchAnimalDetails(AnimalDetailsUseCase.Tab.DETAILS) }
    }

    @Test
    fun showPetWarningDialog_firesDialogEvent() {
        every { animal.sex } returns Sex.MALE
        every { resourceProvider.getSexString(R.string.pet_warning_dialog_body, Sex.MALE, "name", SentencePlacement.OBJECT, SentencePlacement.SUBJECT) } returns "name's shelter does not allow for us to search for him directly, so we can\'t tell if he is still available for adoption, sorry!"
        createSubject()
        subject.warning.set(true)
        val slot = slot<DialogEvent>()
        every { eventBus += capture(slot) } answers { nothing }

        subject.showPetWarningDialog(mockk())

        slot.captured.apply {
            assertThat(titleText).isEqualTo(R.string.pet_warning_dialog_title)
            assertThat(bodyText).isEqualTo("name's shelter does not allow for us to search for him directly, so we can\'t tell if he is still available for adoption, sorry!")
            assertThat(positiveButtonText).isEqualTo(R.string.pet_warning_dialog_contact)
            assertThat(negativeButtonText).isEqualTo(R.string.dialog_cancel)
            assertThat(imageUrl).isEqualTo("photo.jpg")
            assertThat(primaryColor).isEqualTo(R.color.warning)
        }
    }

    @Test
    fun showPetWarningDialog_clickPositiveButton_launchAnimalDetailsToContactTab() {
        every { animal.sex } returns Sex.MALE
        every { resourceProvider.getSexString(R.string.pet_warning_dialog_body, Sex.MALE, "name", SentencePlacement.OBJECT, SentencePlacement.SUBJECT) } returns "name's shelter does not allow for us to search for him directly, so we can\'t tell if he is still available for adoption, sorry!"
        createSubject()
        subject.warning.set(true)
        val slot = slot<DialogEvent>()
        every { eventBus += capture(slot) } answers { nothing }
        subject = spyk(subject)

        subject.showPetWarningDialog(mockk())
        slot.captured.positiveButtonCallback?.invoke()

        verify { subject.launchAnimalDetails(AnimalDetailsUseCase.Tab.CONTACT) }
    }

    private fun createSubject() {
        subject = AnimalListItemViewModel(animal, eventBus, dataStore, resourceProvider)
    }
}