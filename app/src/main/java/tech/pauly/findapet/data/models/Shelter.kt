package tech.pauly.findapet.data.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "shelter", strict = false)
open class Shelter {

    @field:Element
    open lateinit var id: String
        protected set

    @field:Element
    open lateinit var name: String
        protected set

    @field:Element(required = false)
    open var address1: String? = null
        protected set

    @field:Element(required = false)
    open var address2: String? = null
        protected set

    @field:Element
    open lateinit var city: String
        protected set

    @field:Element
    open lateinit var state: String
        protected set

    @field:Element
    open lateinit var zip: String
        protected set

    @field:Element
    open lateinit var country: String
        protected set

    @field:Element
    open var latitude: Double = 0.0
        protected set

    @field:Element
    open var longitude: Double = 0.0
        protected set

    @field:Element(required = false)
    open lateinit var phone: String
        protected set

    @field:Element(required = false)
    open lateinit var fax: String
        protected set

    @field:Element(required = false)
    open lateinit var email: String
        protected set
}