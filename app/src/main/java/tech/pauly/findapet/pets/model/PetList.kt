package tech.pauly.findapet.pets.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class PetList(
    @Json(name = "animals") val pets: List<Pet>?,
    val pagination: Pagination?
)
