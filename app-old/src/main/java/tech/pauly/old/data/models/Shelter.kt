package tech.pauly.old.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Entity
@Root(name = "shelter", strict = false)
open class Shelter {

    @PrimaryKey
    @field:Element(required = false)
    open lateinit var id: String

    @field:Element(required = false)
    open var name: String? = null

    @field:Element(required = false)
    open var address1: String? = null

    @field:Element(required = false)
    open var address2: String? = null

    @field:Element(required = false)
    open var city: String? = null

    @field:Element(required = false)
    open var state: String? = null

    @field:Element(required = false)
    open var zip: String? = null

    @field:Element(required = false)
    open var country: String? = null

    @field:Element(required = false)
    open var latitude: Double? = null

    @field:Element(required = false)
    open var longitude: Double? = null

    @field:Element(required = false)
    open var phone: String? = null

    @field:Element(required = false)
    open var fax: String? = null

    @field:Element(required = false)
    open var email: String? = null

    open val geocodingAddress: String?
        get() {
            return if (city == null || state == null) {
                "$zip"
            } else if (address1 != null) {
                "$address1 $city, $state"
            } else {
                "$city, $state"
            }
        }

    open val formattedAddress: String
        get() = "${address1?.let { "$it${address2?.let { " $it" } ?: ""}\n" } ?: ""}$city, $state $zip"
}