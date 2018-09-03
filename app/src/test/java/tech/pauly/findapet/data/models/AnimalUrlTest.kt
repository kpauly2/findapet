package tech.pauly.findapet.data.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AnimalUrlTest {

    @Test
    fun urlDoesNotContainLocalCharacters_returnsExactUrl() {
        val url = "!@#$%^&*)()+=`~',./<>?:\"[]{}\\|qwertyuiopasdfghjklzxcvbnm"
        assertThat(url.fullUrl).isEqualTo(url)
    }

    @Test
    fun urlContainsPng_returnsExactUrl() {
        val url = "!@#$%^&*)()+=`~',./<>?:\"[]{}\\|qwertyuiopasdfghjklzxcvbnm.png"
        assertThat(url.fullUrl).isEqualTo(url)
    }

    @Test
    fun urlContainsDash_returnsExactUrl() {
        val url = "!@#$%^&*)()+=`~',./<>?:\"[]{}\\|qwertyuiopasdfghjklzxcvbnm-"
        assertThat(url.fullUrl).isEqualTo(url)
    }

    @Test
    fun urlContainsDashAndPng_returnsLocalVersionOfUrl() {
        val url = "!@#$%^&*)()+=`~',./<>?:\"[]{}\\|qwertyuiopasdfghjklzxcvbnm-.png"
        assertThat(url.fullUrl).isEqualTo("file://$url")
    }
}