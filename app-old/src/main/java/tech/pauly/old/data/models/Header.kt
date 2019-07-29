package tech.pauly.old.data.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "header", strict = false)
open class Header {

    @field:Element
    open var version: String? = null
        protected set

    @field:Element
    open var timestamp: String? = null
        protected set

    @field:Element
    open var status: Status? = null
        protected set
}
