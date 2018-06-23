package tech.pauly.findapet.data

import io.reactivex.Observable
import tech.pauly.findapet.BuildConfig
import tech.pauly.findapet.data.models.ShelterListResponse
import javax.inject.Inject

private const val SHELTER_RETURN_COUNT = 20

open class ShelterRepository @Inject
internal constructor(private val shelterService: ShelterService,
                     private val observableHelper: ObservableHelper) {

    open fun fetchShelters(location: String): Observable<ShelterListResponse> {
        return shelterService.fetchShelters(location,
                BuildConfig.API_KEY,
                SHELTER_RETURN_COUNT)
                .compose(observableHelper.applySingleSchedulers())
                .toObservable()
    }
}