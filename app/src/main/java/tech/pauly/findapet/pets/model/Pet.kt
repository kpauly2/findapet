package tech.pauly.findapet.pets.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import tech.pauly.findapet.shelters.model.Contact
import java.util.*

@JsonClass(generateAdapter = true)
class Pet(
    val id: Long?,
    val name: String?,
    val url: String?,
    val type: PetType?,
    val species: String?,
    val breeds: BreedGroup?,
    val colors: ColorGroup?,
    val age: Age?,
    val gender: Gender?,
    val size: Size?,
    val coat: Coat?,
    val attributes: AttributeGroup?,
    val environment: Environment?,
    val tags: List<String>?,
    val description: String?,
    val photos: List<Photo>?,
    val status: AdoptionStatus,
    val contact: Contact?,
    @Json(name = "organization_id") val shelterId: String?,
    @Json(name = "published_at") val publishedAt: Date?
) {

    val listPhoto: String?
        get() {
            return photos?.firstOrNull { it.large != null }?.large
                ?: photos?.firstOrNull { it.medium != null }?.medium
        }

    val details: String?
        get() {
            val formattedAge = age?.name?.toLowerCase()?.capitalize()
            val formattedBreed = breeds?.primary?.let { " | $it" } ?: ""
            return "$formattedAge$formattedBreed"
        }
}

@JsonClass(generateAdapter = true)
class Photo(
    val small: String?,
    val medium: String?,
    val large: String?,
    val full: String?
)

@JsonClass(generateAdapter = true)
class Environment(
    val children: Boolean?,
    val dogs: Boolean?,
    val cats: Boolean?
)

@JsonClass(generateAdapter = true)
class AttributeGroup(
    val declawed: Boolean?,
    @Json(name = "spayed_neutered") val spayedNeutered: Boolean?,
    @Json(name = "house_trained") val houseTrained: Boolean?,
    @Json(name = "special_needs") val specialNeeds: Boolean?,
    @Json(name = "shots_current") val shotsCurrent: Boolean
)

@JsonClass(generateAdapter = true)
class ColorGroup(
    val primary: String?,
    val secondary: String?,
    val tertiary: String?
)

@JsonClass(generateAdapter = true)
class BreedGroup(
    val primary: String?,
    val secondary: String?,
    val mixed: Boolean?,
    val unknown: Boolean?
)

enum class AdoptionStatus {
    @Json(name = "adoptable") ADOPTABLE,
    @Json(name = "adopted") ADOPTED,
    @Json(name = "found") FOUND,
}

enum class Coat {
    @Json(name = "Short") SHORT,
    @Json(name = "Medium") MEDIUM,
    @Json(name = "Long") LONG,
    @Json(name = "Wire") WIRE,
    @Json(name = "Hairless") HAIRLESS,
    @Json(name = "Curly") CURLY,
}

enum class Size {
    @Json(name = "Small") SMALL,
    @Json(name = "Medium") MEDIUM,
    @Json(name = "Large") LARGE,
    @Json(name = "Extra Large") EXTRA_LARGE,
}

enum class Gender {
    @Json(name = "Male") MALE,
    @Json(name = "Female") FEMALE,
    @Json(name = "Unknown") UNKNOWN,
}

enum class Age {
    @Json(name = "Baby") BABY,
    @Json(name = "Young") YOUNG,
    @Json(name = "Adult") ADULT,
    @Json(name = "Senior") SENIOR
}

enum class PetType {
    @Json(name = "Dog") DOG,
    @Json(name = "Cat") CAT,
    @Json(name = "Rabbit") RABBIT,
    @Json(name = "Small & Furry") SMALL_AND_FURRY,
    @Json(name = "Horse") HORSE,
    @Json(name = "Bird") BIRD,
    @Json(name = "Barnyard") BARNYARD,
    @Json(name = "Scales, Fins & Other") SCALES_FINS_AND_OTHER,
}