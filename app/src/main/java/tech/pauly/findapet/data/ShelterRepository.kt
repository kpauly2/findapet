package tech.pauly.findapet.data

import androidx.room.EmptyResultSetException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import tech.pauly.findapet.BuildConfig
import tech.pauly.findapet.data.models.*
import javax.inject.Inject
import javax.inject.Singleton

private const val SHELTER_RETURN_COUNT = 20

@Singleton
open class ShelterRepository @Inject
internal constructor(private val shelterService: ShelterService,
                     private val observableHelper: ObservableHelper,
                     private val database: FavoriteDatabase) {

    open fun fetchShelters(location: String): Observable<ShelterListResponse> {
        return shelterService.fetchShelters(location,
                BuildConfig.API_KEY,
                SHELTER_RETURN_COUNT)
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }

    open fun fetchShelter(animal: Animal): Observable<Shelter> {
        return database.shelterDao().getShelter(animal.shelterId)
                .onErrorResumeNext {
                    if (it is EmptyResultSetException) {
                        shelterService.fetchShelter(BuildConfig.API_KEY, animal.shelterId).map {
                            if (it.header?.status?.code == StatusCode.PFAPI_ERR_UNAUTHORIZED) {
                                throw PetfinderException(StatusCode.PFAPI_ERR_UNAUTHORIZED)
                            }
                            it.shelter
                        }
                    } else {
                        throw it
                    }
                }
                .onErrorResumeNext {
                    if (it is PetfinderException && it.statusCode == StatusCode.PFAPI_ERR_UNAUTHORIZED) {
                        Single.just(animal.contact)
                    } else {
                        throw it
                    }
                }
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }

    open fun insertShelterRecordForAnimal(localAnimal: LocalAnimal): Completable {
        return fetchShelter(localAnimal).flatMap {
            Observable.fromCallable { database.shelterDao().insert(it) }
                    .compose(observableHelper.applyObservableSchedulers())
        }.ignoreElements()
    }

    open fun deleteShelterIfNecessary(savedAnimalShelters: List<String>, shelterToDelete: String): Completable {
        return if (savedAnimalShelters.count { it == shelterToDelete } == 1) {
            Completable.fromCallable { database.shelterDao().delete(shelterToDelete) }
                    .compose(observableHelper.applyCompletableSchedulers())
        } else {
            Completable.complete()
        }
    }
}