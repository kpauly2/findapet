package tech.pauly.findapet.data

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.data.models.Favorite

class FavoriteRepositoryTest {

    private val database: FavoriteDatabase = mock()
    private val observableHelper: ObservableHelper = mock()
    private val favoriteDao: FavoriteDao = mock()

    private lateinit var subject: FavoriteRepository

    @Before
    fun setup() {
        whenever(observableHelper.applySingleSchedulers<Any>()).thenReturn(SingleTransformer { it })
        whenever(database.favoriteDao()).thenReturn(favoriteDao)

        subject = FavoriteRepository(database, observableHelper)
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
    fun favoriteAnimal_insertsRecordWithAnimalId() {
        val observer = subject.favoriteAnimal(10).test()

        observer.assertComplete()
        verify(favoriteDao).insert(Favorite(animalId = 10))
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun unfavoriteAnimal_deleteRecordWithAnimalId() {
        val observer = subject.unfavoriteAnimal(10).test()

        observer.assertComplete()
        verify(favoriteDao).delete(10)
        verify(observableHelper).applySingleSchedulers<Any>()
    }
}