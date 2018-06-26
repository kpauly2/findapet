package tech.pauly.findapet.data

import io.reactivex.Completable
import io.reactivex.Single
import tech.pauly.findapet.data.models.Favorite
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FavoriteRepository @Inject
internal constructor(private val database: FavoriteDatabase,
                     private val observableHelper: ObservableHelper) {

    open fun isAnimalFavorited(animalId: Int): Single<Boolean> {
        return database.favoriteDao().isFavorited(animalId)
                .compose(observableHelper.applySingleSchedulers())
    }

    open fun favoriteAnimal(animalId: Int): Completable {
        return Single.fromCallable { database.favoriteDao().insert(Favorite(animalId = animalId)) }
                .compose(observableHelper.applySingleSchedulers())
                .ignoreElement()
    }

    open fun unfavoriteAnimal(animalId: Int): Completable {
        return Single.fromCallable { database.favoriteDao().delete(animalId = animalId) }
                .compose(observableHelper.applySingleSchedulers())
                .ignoreElement()
    }
}