package tech.pauly.old.data

import io.reactivex.Observable
import tech.pauly.old.BuildConfig
import tech.pauly.old.data.models.*
import javax.inject.Inject

private const val ANIMAL_RETURN_COUNT = 20

open class AnimalRepository @Inject
constructor(private val animalService: AnimalService,
            private val observableHelper: ObservableHelper) {

    open fun fetchAnimals(request: FetchAnimalsRequest): Observable<AnimalListResponse> {
        val filter = request.filter
        return animalService.fetchAnimals(request.location,
                BuildConfig.API_KEY,
                request.animalType.serverName,
                request.lastOffset,
                ANIMAL_RETURN_COUNT,
                filter.sex.serverName,
                filter.age.serverName,
                filter.size.serverName,
                filter.breed)
                .map {
                    it.header.status?.code?.let {
                        if (it != StatusCode.PFAPI_OK) throw PetfinderException(it)
                    }
                    if (it.animalList == null || it.animalList?.size == 0) {
                        throw PetfinderException(StatusCode.ERR_NO_ANIMALS)
                    }
                    it
                }
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }

    open fun fetchAnimal(animal: Animal): Observable<AnimalResponseWrapper> {
        return animalService.fetchAnimal(BuildConfig.API_KEY, animal.id.toString())
                .map {
                    val responseAnimal = it.animal ?: animal
                    AnimalResponseWrapper(responseAnimal, it.header)
                }
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }
}

