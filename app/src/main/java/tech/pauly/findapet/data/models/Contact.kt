package tech.pauly.findapet.data.models

import org.simpleframework.xml.Element

open class Contact {

    @field:Element(required = false)
    open var address1: String? = null
        protected set

    @field:Element(required = false)
    open var address2: String? = null
        protected set

    @field:Element(required = false)
    open var city: String? = null
        protected set

    @field:Element(required = false)
    open var state: String? = null
        protected set

    @field:Element(required = false)
    open var zip: String? = null
        protected set

    @field:Element(required = false)
    open var phone: String? = null
        protected set

    @field:Element(required = false)
    open var fax: String? = null
        protected set

    @field:Element(required = false)
    open var email: String? = null
        protected set
}
