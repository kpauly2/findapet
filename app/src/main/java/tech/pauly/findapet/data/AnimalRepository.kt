package tech.pauly.findapet.data

import io.reactivex.Observable
import tech.pauly.findapet.BuildConfig
import tech.pauly.findapet.data.models.AnimalListResponse
import tech.pauly.findapet.data.models.FetchAnimalsRequest
import tech.pauly.findapet.data.models.SingleAnimalResponseWrapper
import tech.pauly.findapet.data.models.StatusCode
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

    open fun fetchAnimal(id: Int): Observable<SingleAnimalResponseWrapper> {
        return animalService.fetchAnimal(BuildConfig.API_KEY, id.toString())
                .map { SingleAnimalResponseWrapper(it, id) }
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }
}

