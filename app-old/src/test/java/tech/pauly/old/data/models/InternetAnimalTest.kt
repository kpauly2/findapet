package tech.pauly.old.data.models

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import tech.pauly.old.shared.ResourceProvider


class InternetAnimalTest {

    private val resourceProvider: ResourceProvider = mock()

    private var subject = InternetAnimal()

    @Test
    fun formattedBreedList_getBreedListWithSeparator() {
        subject.breedList = listOf("breed 1", "breed 2")

        assertThat(subject.formattedBreedList).isEqualTo("breed 1 / breed 2")
    }

    @Test
    fun photoUrlList_mediaNull_returnNull() {
        subject.media = null

        assertThat(subject.photoUrlList).isNull()
    }

    @Test
    fun photoUrlList_photoListNull_returnNull() {
        val media: Media = mock {
            on { photoList }.thenReturn(null)
        }
        subject.media = media

        assertThat(subject.photoUrlList).isNull()
    }

    @Test
    fun photoUrlList_onlyPhotosOfUnwantedSizesPresent_returnNull() {
        val photo1: Photo = mock {
            on { size }.thenReturn(PhotoSize.THUMBNAIL)
        }
        val photo2: Photo = mock {
            on { size }.thenReturn(PhotoSize.PETNOTE)
        }
        val photo3: Photo = mock {
            on { size }.thenReturn(PhotoSize.PET_NOTE_THUMBNAIL)
        }
        val photo4: Photo = mock {
            on { size }.thenReturn(PhotoSize.FEATURED_PET_MODULE)
        }
        val media: Media = mock {
            on { photoList }.thenReturn(listOf(photo1, photo2, photo3, photo4))
        }
        subject.media = media

        assertThat(subject.photoUrlList).isNull()
    }

    @Test
    fun photoUrlList_photoListHasLargePhotos_returnsOnlyLargePhotos() {
        val photo1: Photo = mock {
            on { size }.thenReturn(PhotoSize.THUMBNAIL)
        }
        val photo2: Photo = mock {
            on { size }.thenReturn(PhotoSize.PETNOTE)
        }
        val photo3: Photo = mock {
            on { size }.thenReturn(PhotoSize.PET_NOTE_THUMBNAIL)
        }
        val photo4: Photo = mock {
            on { size }.thenReturn(PhotoSize.FEATURED_PET_MODULE)
        }
        val photo5: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo5.jpg")
        }
        val photo6: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo6.jpg")
        }
        val media: Media = mock {
            on { photoList }.thenReturn(listOf(photo1, photo2, photo3, photo4, photo5, photo6))
        }
        subject.media = media

        subject.photoUrlList.also {
            assertThat(it).isNotNull()
            assertThat(it?.size).isEqualTo(2)
            assertThat(it?.get(0)).isEqualTo("photo5.jpg")
            assertThat(it?.get(1)).isEqualTo("photo6.jpg")
        }
    }

    @Test
    fun primaryPhotoUrl_photoUrlListEmpty_returnNull() {
        subject.media = null

        assertThat(subject.primaryPhotoUrl).isNull()
    }

    @Test
    fun primaryPhotoUrl_photoUrlListHasElements_getFirstPhotoFromUrlList() {
        val photo5: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo5.jpg")
        }
        val photo6: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo6.jpg")
        }
        val media: Media = mock {
            on { photoList }.thenReturn(listOf(photo5, photo6))
        }
        subject.media = media

        assertThat(subject.primaryPhotoUrl).isEqualTo("photo5.jpg")
    }

    @Test
    fun deleteLocalPhotos_photoListNull_doNothing() {
        subject.media = null

        subject.deleteLocalPhotos(resourceProvider)

        verify(resourceProvider, never()).deleteFile(any())
    }

    @Test
    fun deleteLocalPhotos_photoListHasPhotos_deleteFileForEachPhoto() {
        val photo5: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo5.jpg")
        }
        val photo6: Photo = mock {
            on { size }.thenReturn(PhotoSize.LARGE)
            on { url }.thenReturn("photo6.jpg")
        }
        val media: Media = mock {
            on { photoList }.thenReturn(listOf(photo5, photo6))
        }
        subject.media = media
        subject.id = 10

        subject.deleteLocalPhotos(resourceProvider)

        verify(resourceProvider).deleteFile("10-0.png")
        verify(resourceProvider).deleteFile("10-1.png")
    }
}