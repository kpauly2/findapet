package tech.pauly.old.data

import javax.inject.Inject

import io.reactivex.Single
import tech.pauly.old.BuildConfig
import tech.pauly.old.data.models.AnimalType
import tech.pauly.old.data.models.BreedListResponse

open class BreedRepository @Inject
internal constructor(private val breedService: BreedService,
                     private val observableHelper: ObservableHelper) {

    open fun getBreedList(animalType: AnimalType): Single<BreedListResponse> {
        return breedService.fetchBreeds(BuildConfig.API_KEY, animalType.serverName)
                .compose(observableHelper.applySingleSchedulers())
    }
}
