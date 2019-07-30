package tech.pauly.findapet.shelters.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Address(
    val address1: String?,
    val address2: String?,
    val city: String?,
    val state: String?,
    val postcode: String?,
    val country: String?
)