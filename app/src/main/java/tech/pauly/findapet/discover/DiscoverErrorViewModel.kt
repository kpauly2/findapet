package tech.pauly.findapet.discover

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.StatusCode
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.datastore.UseCase
import javax.inject.Inject

open class DiscoverErrorViewModel @Inject
constructor(private val dataStore: TransientDataStore) : BaseViewModel() {

    var errorVisible = ObservableBoolean(false)
    var errorTitle = ObservableInt(R.string.missing)
    var errorBody1 = ObservableInt(R.string.missing)
    var errorBody2 = ObservableInt(R.string.missing)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribeToErrorEvents() {
        dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class)
                .quickSubscribe {
                    if (it.statusCode == null) hideError() else showError(it.statusCode)
                }
    }

    private fun showError(statusCode: StatusCode) {
        errorTitle.set(when(statusCode) {
            StatusCode.ERR_NO_ANIMALS -> R.string.no_animals_title
            StatusCode.ERR_NO_LOCATION -> R.string.no_location_title
            StatusCode.ERR_FETCH_LOCATION -> R.string.location_error_title
            StatusCode.ERR_LOCAL -> R.string.generic_error_title
            else -> R.string.backend_error_title
        })
        errorBody1.set(when(statusCode) {
            StatusCode.ERR_NO_ANIMALS -> R.string.no_animals_body1
            StatusCode.ERR_NO_LOCATION -> R.string.no_location_body1
            StatusCode.ERR_FETCH_LOCATION -> R.string.location_error_body1
            StatusCode.ERR_LOCAL -> R.string.generic_error_body1
            else -> R.string.backend_error_body1
        })
        errorBody2.set(when(statusCode) {
            StatusCode.ERR_NO_ANIMALS -> R.string.no_animals_body2
            StatusCode.ERR_NO_LOCATION -> R.string.no_location_body2
            StatusCode.ERR_FETCH_LOCATION -> R.string.location_error_body2
            StatusCode.ERR_LOCAL -> R.string.generic_error_body2
            else -> R.string.backend_error_body2
        })
        errorVisible.set(true)
    }

    private fun hideError() {
        errorVisible.set(false)
    }
}

open class DiscoverErrorUseCase(val statusCode: StatusCode?) : UseCase {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiscoverErrorUseCase

        if (statusCode != other.statusCode) return false

        return true
    }

    override fun hashCode(): Int {
        return statusCode?.hashCode() ?: 0
    }
}
