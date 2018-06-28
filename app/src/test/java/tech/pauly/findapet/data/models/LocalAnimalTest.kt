package tech.pauly.findapet.data.models

import android.graphics.Bitmap
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import tech.pauly.findapet.shared.ResourceProvider

class LocalAnimalTest {

    private val resourceProvider: ResourceProvider = mock()
    private val contact: Contact = mock()

    private var subject = LocalAnimal()

    @Test
    fun photoUrlList_getBackingPhotoList() {
        subject.photoList = listOf("1", "2")

        Assertions.assertThat(subject.photoUrlList).isEqualTo(subject.photoList)
    }

    @Test
    fun primaryPhotoUrl_photoUrlListEmpty_returnNull() {
        subject.photoList = null

        Assertions.assertThat(subject.primaryPhotoUrl).isNull()
    }

    @Test
    fun primaryPhotoUrl_photoUrlListHasElements_getFirstPhotoFromUrlList() {
        subject.photoList = listOf("10-0.png", "10-1.png")

        Assertions.assertThat(subject.primaryPhotoUrl).isEqualTo("10-0.png")
    }

    @Test
    fun formattedBreedList_returnsBackingFormattedBreedList() {
        subject._formattedBreedList = "breed / breed"

        Assertions.assertThat(subject.formattedBreedList).isEqualTo(subject._formattedBreedList)
    }

    @Test
    fun deleteLocalPhotos_photoListNull_doNothing() {
        subject.photoList = null

        subject.deleteLocalPhotos(resourceProvider)

        verify(resourceProvider, never()).deleteFile(any())
    }

    @Test
    fun deleteLocalPhotos_photoListHasPhotos_deleteFileForEachPhoto() {
        subject.photoList = listOf("10-0.png", "10-1.png")

        subject.deleteLocalPhotos(resourceProvider)

        verify(resourceProvider).deleteFile("10-0.png")
        verify(resourceProvider).deleteFile("10-1.png")
    }

    @Test
    fun fromInternalAnimal_assignsBasicValuesFromInternetAnimal() {
        val internetAnimal = setupInternetAnimal(contact)

        val observer = subject.fromInternetAnimal(internetAnimal, resourceProvider).test()

        val value = observer.values()[0]
        assertThat(value.id).isEqualTo(10)
        assertThat(value.shelterId).isEqualTo("shelterid")
        assertThat(value.name).isEqualTo("name")
        assertThat(value._type).isEqualTo("type")
        assertThat(value.formattedBreedList).isEqualTo("breed1 / breed2")
        assertThat(value.mix).isEqualTo("mix")
        assertThat(value._age).isEqualTo("age")
        assertThat(value._sex).isEqualTo("sex")
        assertThat(value._size).isEqualTo("size")
        assertThat(value._options).isEqualTo(listOf("options"))
        assertThat(value.contact).isEqualTo(contact)
        assertThat(value.shelterPetId).isEqualTo("shelterpetid")
        assertThat(value.description).isEqualTo("description")
    }

    @Test
    fun fromInternalAnimal_photoUrlListNull_setPhotoListNull() {
        val internetAnimal = setupInternetAnimal(contact)

        val observer = subject.fromInternetAnimal(internetAnimal, resourceProvider).test()

        val value = observer.values()[0]
        assertThat(value.photoList).isNull()
    }

    @Test
    fun fromInternalAnimal_photoListValid_savesBitmapForEachPhotoAndReturnsFilenames() {
        val bitmap5: Bitmap = mock()
        val bitmap6: Bitmap = mock()
        whenever(resourceProvider.getBitmapFromUrl("photo5.jpg")).thenReturn(bitmap5)
        whenever(resourceProvider.getBitmapFromUrl("photo6.jpg")).thenReturn(bitmap6)
        whenever(resourceProvider.saveBitmapToFile(eq("10-0.png"), any())).thenReturn("local/10-0.png")
        whenever(resourceProvider.saveBitmapToFile(eq("10-1.png"), any())).thenReturn("local/10-1.png")
        val photo5: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo5.jpg")
        }
        val photo6: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo6.jpg")
        }
        val internetAnimal = setupInternetAnimal(contact)
        internetAnimal.media = mock {
            on { photoList }.thenReturn(listOf(photo5, photo6))
        }

        val observer = subject.fromInternetAnimal(internetAnimal, resourceProvider).test()

        val value = observer.values()[0]
        verify(resourceProvider).getBitmapFromUrl("photo5.jpg")
        verify(resourceProvider).getBitmapFromUrl("photo6.jpg")
        verify(resourceProvider).saveBitmapToFile("10-0.png", bitmap5)
        verify(resourceProvider).saveBitmapToFile("10-1.png", bitmap6)
        assertThat(value.photoList).isEqualTo(listOf("local/10-0.png", "local/10-1.png"))
    }

    private fun setupInternetAnimal(contact: Contact): InternetAnimal {
        return InternetAnimal().apply {
            id = 10
            shelterId = "shelterid"
            name = "name"
            _type = "type"
            breedList = listOf("breed1", "breed2")
            mix = "mix"
            _age = "age"
            _sex = "sex"
            _size = "size"
            _options = listOf("options")
            this.contact = contact
            shelterPetId = "shelterpetid"
            description = "description"
            media = null
        }
    }
}