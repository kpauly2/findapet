package tech.pauly.findapet.discover

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.StatusCode
import tech.pauly.findapet.shared.datastore.TransientDataStore

internal class DiscoverErrorViewModelTest {

    private val dataStore: TransientDataStore = mock()

    private lateinit var subject: DiscoverErrorViewModel

    @Before
    fun setup() {
        subject = DiscoverErrorViewModel(dataStore)
    }

    @Test
    fun subscribeToErrorEvents_getsNullStatusCodeError_hideError() {
        whenever(dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class))
                .thenReturn(Observable.just(DiscoverErrorUseCase(null)))
        subject.errorVisible.set(true)

        subject.subscribeToErrorEvents()

        assertThat(subject.errorVisible.get()).isFalse()
    }

    @Test
    fun subscribeToErrorEvents_getsValidStatusCodeError_showError() {
        whenever(dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class))
                .thenReturn(Observable.just(DiscoverErrorUseCase(StatusCode.PFAPI_ERR_INTERNAL)))

        subject.subscribeToErrorEvents()

        assertThat(subject.errorVisible.get()).isTrue()
    }

    @Test
    fun subscribeToErrorEvents_getsNoAnimalsError_showError() {
        whenever(dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class))
                .thenReturn(Observable.just(DiscoverErrorUseCase(StatusCode.ERR_NO_ANIMALS)))

        subject.subscribeToErrorEvents()

        assertThat(subject.errorTitle.get()).isEqualTo(R.string.no_animals_title)
        assertThat(subject.errorBody1.get()).isEqualTo(R.string.no_animals_body1)
        assertThat(subject.errorBody2.get()).isEqualTo(R.string.no_animals_body2)
    }

    @Test
    fun subscribeToErrorEvents_getsNoLocationError_showError() {
        whenever(dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class))
                .thenReturn(Observable.just(DiscoverErrorUseCase(StatusCode.ERR_NO_LOCATION)))

        subject.subscribeToErrorEvents()

        assertThat(subject.errorTitle.get()).isEqualTo(R.string.no_location_title)
        assertThat(subject.errorBody1.get()).isEqualTo(R.string.no_location_body1)
        assertThat(subject.errorBody2.get()).isEqualTo(R.string.no_location_body2)
    }

    @Test
    fun subscribeToErrorEvents_getsFetchLocationError_showError() {
        whenever(dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class))
                .thenReturn(Observable.just(DiscoverErrorUseCase(StatusCode.ERR_FETCH_LOCATION)))

        subject.subscribeToErrorEvents()

        assertThat(subject.errorTitle.get()).isEqualTo(R.string.location_error_title)
        assertThat(subject.errorBody1.get()).isEqualTo(R.string.location_error_body1)
        assertThat(subject.errorBody2.get()).isEqualTo(R.string.location_error_body2)
    }

    @Test
    fun subscribeToErrorEvents_getsLocalError_showError() {
        whenever(dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class))
                .thenReturn(Observable.just(DiscoverErrorUseCase(StatusCode.ERR_LOCAL)))

        subject.subscribeToErrorEvents()

        assertThat(subject.errorTitle.get()).isEqualTo(R.string.generic_error_title)
        assertThat(subject.errorBody1.get()).isEqualTo(R.string.generic_error_body1)
        assertThat(subject.errorBody2.get()).isEqualTo(R.string.generic_error_body2)
    }

    @Test
    fun subscribeToErrorEvents_getsOtherError_showError() {
        whenever(dataStore.observeAndGetUseCase(DiscoverErrorUseCase::class))
                .thenReturn(Observable.just(DiscoverErrorUseCase(StatusCode.PFAPI_ERR_INVALID)))

        subject.subscribeToErrorEvents()

        assertThat(subject.errorTitle.get()).isEqualTo(R.string.backend_error_title)
        assertThat(subject.errorBody1.get()).isEqualTo(R.string.backend_error_body1)
        assertThat(subject.errorBody2.get()).isEqualTo(R.string.backend_error_body2)
    }
}