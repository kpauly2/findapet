package tech.pauly.old.data.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "photo", strict = false)
open class Photo {

    @field:Attribute(name = "id")
    open lateinit var id: String
        protected set

    @field:Attribute(name = "size")
    private lateinit var _size: String
    open val size: PhotoSize
        get() = PhotoSize.fromString(_size)

    @field:Text
    open lateinit var url: String
        protected set
}
