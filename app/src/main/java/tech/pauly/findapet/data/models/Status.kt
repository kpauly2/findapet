package tech.pauly.findapet.data.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "status", strict = false)
open class Status {

    @field:Element(name = "code")
    private var _code: Int = 0
    open val code: StatusCode
        get() = StatusCode.fromInt(_code)

    @field:Element(required = false)
    open var message: String? = null
}