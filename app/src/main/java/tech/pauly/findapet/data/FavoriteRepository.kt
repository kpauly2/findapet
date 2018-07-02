package tech.pauly.findapet.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.simpleframework.xml.core.Complete
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
                     private val resourceProvider: ResourceProvider,
                     private val shelterRepository: ShelterRepository) {

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
        return save.compose(observableHelper.applySingleSchedulers())
                .flatMapCompletable {
                    Completable.fromCallable { database.favoriteDao().insert(it) }
                            .compose(observableHelper.applyCompletableSchedulers())
                            .mergeWith(shelterRepository.insertShelterRecordForAnimal(it))
                }
                .compose(observableHelper.applyCompletableSchedulers())
    }

    open fun unfavoriteAnimal(animal: Animal): Completable {
        return getFavoritedAnimals()
                .flatMapCompletable {
                    animal.deleteLocalPhotos(resourceProvider)
                    Completable.fromCallable { database.favoriteDao().delete(animal.id) }
                            .compose(observableHelper.applyCompletableSchedulers())
                            .mergeWith(deleteShelterIfNecessary(it, animal))
                }
                .compose(observableHelper.applyCompletableSchedulers())
    }

    open fun getFavoritedAnimals(): Observable<List<LocalAnimal>> {
        return database.favoriteDao()
                .getAll()
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }

    private fun deleteShelterIfNecessary(it: List<LocalAnimal>, animal: Animal): Completable {
        return shelterRepository.deleteShelterIfNecessary(it.map { it.shelterId }.distinct(), animal.shelterId)
    }
}