package tech.pauly.findapet.shelters.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Contact(
    val email: String?,
    val phone: String?,
    val address: Address
)