package tech.pauly.old.data.models

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ShelterTest {

    @Test
    fun geocodingAddress_cityNull_returnZip() {
        val subject = Shelter().apply {
            city = null
            state = "state"
            zip = "zip"
        }

        assertThat(subject.geocodingAddress).isEqualTo("zip")
    }

    @Test
    fun geocodingAddress_stateNull_returnZip() {
        val subject = Shelter().apply {
            city = "city"
            state = null
            zip = "zip"
        }

        assertThat(subject.geocodingAddress).isEqualTo("zip")
    }

    @Test
    fun geocodingAddress_cityAndStateNotNullAndAddress1Null_returnCityState() {
        val subject = Shelter().apply {
            city = "city"
            state = "state"
            address1 = null
            zip = "zip"
        }

        assertThat(subject.geocodingAddress).isEqualTo("city, state")
    }

    @Test
    fun geocodingAddress_cityAndStateAndAddress1NotNull_returnAddress() {
        val subject = Shelter().apply {
            city = "city"
            state = "state"
            address1 = "address1"
            zip = "zip"
        }

        assertThat(subject.geocodingAddress).isEqualTo("address1 city, state")
    }

    @Test
    fun formattedAddress_hasFullAddress_returnsFormattedAddress() {
        val subject = Shelter().apply {
            address1 = "address1"
            address2 = "address2"
            city = "city"
            state = "state"
            zip = "zip"
        }

        assertThat(subject.formattedAddress).isEqualTo("address1 address2\ncity, state zip")
    }

    @Test
    fun formattedAddress_missingAddress2_returnsFormattedAddress() {
        val subject = Shelter().apply {
            address1 = "address1"
            city = "city"
            state = "state"
            zip = "zip"
        }

        Assertions.assertThat(subject.formattedAddress).isEqualTo("address1\ncity, state zip")
    }

    @Test
    fun formattedAddress_missingAddress1_returnsFormattedAddress() {
        val subject = Shelter().apply {
            city = "city"
            state = "state"
            zip = "zip"
        }

        Assertions.assertThat(subject.formattedAddress).isEqualTo("city, state zip")
    }
}