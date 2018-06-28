package tech.pauly.findapet.data

import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.data.models.Animal
import tech.pauly.findapet.data.models.InternetAnimal
import tech.pauly.findapet.data.models.LocalAnimal
import tech.pauly.findapet.shared.ResourceProvider

class FavoriteRepositoryTest {

    private val database: FavoriteDatabase = mock()
    private val observableHelper: ObservableHelper = mock()
    private val favoriteDao: FavoriteDao = mock()
    private val resourceProvider: ResourceProvider = mock()

    private lateinit var subject: FavoriteRepository

    @Before
    fun setup() {
        whenever(observableHelper.applySingleSchedulers<Any>()).thenReturn(SingleTransformer { it })
        whenever(database.favoriteDao()).thenReturn(favoriteDao)

        subject = FavoriteRepository(database, observableHelper, resourceProvider)
    }

    @Test
    fun isAnimalFavorited_animalNotFavorited_returnsAnimalNotFavorited() {
        whenever(favoriteDao.isFavorited(10)).thenReturn(Single.just(false))

        val observer = subject.isAnimalFavorited(10).test()

        observer.assertValues(false)
        verify(favoriteDao).isFavorited(10)
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun isAnimalFavorited_animalNotFavorited_returnsAnimalFavorited() {
        whenever(favoriteDao.isFavorited(10)).thenReturn(Single.just(true))

        val observer = subject.isAnimalFavorited(10).test()

        observer.assertValues(true)
        verify(favoriteDao).isFavorited(10)
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun favoriteAnimal_forInternetAnimal_insertsLocalAnimalForInternetAnimal() {
        val animal = InternetAnimal().apply {
            id = 10
            shelterId = ""
            name = ""
            _type = ""
            breedList = listOf("")
            mix = ""
            _age = ""
            _sex = ""
            _size = ""
            _options = listOf("")
            contact = mock()
            shelterPetId = ""
            description = ""
            media = null
        }
        val observer = subject.favoriteAnimal(animal).test()

        observer.assertComplete()
        verify(favoriteDao).insert(check {
            assertThat(it.id).isEqualTo(10)
        })
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun favoriteAnimal_forLocalAnimal_insertsLocalAnimal() {
        val animal: LocalAnimal = mock {
            on { id }.thenReturn(10)
        }
        val observer = subject.favoriteAnimal(animal).test()

        observer.assertComplete()
        verify(favoriteDao).insert(check {
            assertThat(it.id).isEqualTo(10)
        })
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun unfavoriteAnimal_deleteAnimalFromDatabaseAndAnimalPhotos() {
        val animal: Animal = mock {
            on { id }.thenReturn(10)
        }

        val observer = subject.unfavoriteAnimal(animal).test()

        observer.assertComplete()
        verify(favoriteDao).delete(10)
        verify(animal).deleteLocalPhotos(resourceProvider)
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun getFavoritedAnimals_getAllAnimalsFromDatabase() {
        val value = listOf(LocalAnimal(), LocalAnimal())
        whenever(favoriteDao.getAll()).thenReturn(Single.just(value))

        val observer = subject.getFavoritedAnimals().test()

        observer.assertValues(value)
        verify(favoriteDao).getAll()
        verify(observableHelper).applySingleSchedulers<Any>()
    }
}