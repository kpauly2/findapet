package tech.pauly.findapet.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import tech.pauly.findapet.data.models.Animal
import tech.pauly.findapet.data.models.InternetAnimal
import tech.pauly.findapet.data.models.LocalAnimal
import tech.pauly.findapet.shared.ResourceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FavoriteRepository @Inject
internal constructor(private val database: FavoriteDatabase,
                     private val observableHelper: ObservableHelper,
                     private val resourceProvider: ResourceProvider) {

    open fun isAnimalFavorited(animalId: Int): Single<Boolean> {
        return database.favoriteDao()
                .isFavorited(animalId)
                .compose(observableHelper.applySingleSchedulers())
    }

    open fun favoriteAnimal(animal: Animal): Completable {
        val save = if (animal is InternetAnimal) {
            LocalAnimal().fromInternetAnimal(animal, resourceProvider)
        } else {
            Single.just(animal as LocalAnimal)
        }
        return save.map { saveLocalAnimal(it) }
                .compose(observableHelper.applySingleSchedulers())
                .ignoreElement()
    }

    open fun unfavoriteAnimal(animal: Animal): Completable {
        return Single.fromCallable {
            database.favoriteDao().delete(animalId = animal.id)
            animal.deleteLocalPhotos(resourceProvider)
        }
                .compose(observableHelper.applySingleSchedulers())
                .ignoreElement()
    }

    open fun getFavoritedAnimals(): Observable<List<LocalAnimal>> {
        return database.favoriteDao()
                .getAll()
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }

    private fun saveLocalAnimal(localAnimal: LocalAnimal): Long {
        return database.favoriteDao().insert(localAnimal)
    }
}