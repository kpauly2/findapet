package tech.pauly.findapet.shared

import android.location.Address
import android.location.Criteria
import com.nhaarman.mockito_kotlin.*
import io.reactivex.ObservableTransformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.data.ObservableHelper
import tech.pauly.findapet.data.PetfinderException
import tech.pauly.findapet.data.models.StatusCode
import java.io.IOException
import java.util.*

class LocationHelperTest {
    private val observableHelper: ObservableHelper = mock()
    private val locationWrapper: LocationWrapper = mock()
    private val localeWrapper: LocaleWrapper = mock()

    private lateinit var subject: LocationHelper

    @Before
    fun setup() {
        whenever(locationWrapper.isEmulator).thenReturn(false)
        whenever(localeWrapper.getLocale()).thenReturn(Locale.US)
        whenever(observableHelper.applyObservableSchedulers<Any>()).thenReturn(ObservableTransformer { it })
        subject = LocationHelper(observableHelper, locationWrapper, localeWrapper)
    }

    @Test
    fun fetchCurrentLocation_onEmulator_doNotFetchNewLocation() {
        whenever(locationWrapper.isEmulator).thenReturn(true)

        subject.fetchCurrentLocation().test()

        verify(locationWrapper, never()).fetchNewLocation(any(), any())
    }

    @Test
    fun fetchCurrentLocation_noLocation_fetchNewLocation() {
        subject.fetchCurrentLocation().test()

        verify(locationWrapper).fetchNewLocation(eq(Criteria.ACCURACY_FINE), any())
    }

    @Test
    fun fetchCurrentLocation_fetchNewLocation_fireLocationSubjectWithAddress() {
        val address = mock<Address>()
        whenever(locationWrapper.getAddressFromLocation(any())).thenReturn(address)
        whenever(locationWrapper.fetchNewLocation(eq(Criteria.ACCURACY_FINE), any())).then {
            subject.updateForNewLocation(mock())
        }

        val observable = subject.fetchCurrentLocation().test()

        observable.assertValue(address)
    }

    @Test
    fun fetchCurrentLocation_fetchNewLocationThrowsError_fireLocationSubjectWithFetchError() {
        whenever(locationWrapper.getAddressFromLocation(any())).thenThrow(IOException())
        whenever(locationWrapper.fetchNewLocation(eq(Criteria.ACCURACY_FINE), any())).then {
            subject.updateForNewLocation(mock())
        }

        val observable = subject.fetchCurrentLocation().test()

        observable.assertError(PetfinderException(StatusCode.ERR_FETCH_LOCATION))
    }

    @Test
    fun fetchCurrentLocation_hasLocation_returnExistingLocationAndDoesNotFetchNewLocation() {
        val address = setupExistingLocation()
        clearInvocations(locationWrapper)

        val observable = subject.fetchCurrentLocation().test()

        observable.assertValue(address)
        verify(locationWrapper, never()).fetchNewLocation(any(), any())
    }

    @Test
    fun getCurrentDistanceToContactInfo_contactInfoNull_returnNegativeOne() {
        setupExistingLocation()
        val observable =  subject.getCurrentDistanceToContactInfo(null).test()

        observable.assertValue(-1)
    }

    @Test
    fun getCurrentDistanceToContactInfo_contactInfoValid_getLocationBetweenCurrentLocationAndContactInfo() {
        val petAddress = mock<Address>()
        val userAddress = setupExistingLocation()
        whenever(locationWrapper.getAddressFromName("city, state")).thenReturn(petAddress)
        whenever(locationWrapper.locationBetweenAddresses(userAddress, petAddress)).thenReturn(10)

        val observable = subject.getCurrentDistanceToContactInfo(mock {
            on { geocodingAddress }.thenReturn("city, state")
        }).test()

        observable.assertValue(10)
    }

    private fun setupExistingLocation(): Address {
        val address = mock<Address>()
        whenever(locationWrapper.getAddressFromLocation(any())).thenReturn(address)
        whenever(locationWrapper.fetchNewLocation(eq(Criteria.ACCURACY_FINE), any())).then {
            subject.updateForNewLocation(mock())
        }
        subject.fetchCurrentLocation().test()
        return address
    }
}