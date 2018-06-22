package tech.pauly.findapet.data.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "pet", strict = false)
open class Animal {

    @field:Element
    open var id: Int = 0
        protected set

    @field:Element
    open lateinit var shelterId: String
        protected set

    @field:Element
    open lateinit var name: String
        protected set

    @field:Element(name = "animal")
    private lateinit var _type: String
    open val type: AnimalType
        get() = AnimalType.fromString(_type)

    @field:ElementList(name = "breeds", entry = "breed")
    open lateinit var breedList: List<String>
        protected set

    open val formattedBreedList: String
        get() = breedList.joinToString(" / ")

    @field:Element
    open lateinit var mix: String
        protected set

    @field:Element(name = "age")
    private lateinit var _age: String
    open val age: Age
        get() = Age.fromString(_age)

    @field:Element(name = "sex")
    private lateinit var _sex: String
    open val sex: Sex
        get() = Sex.fromString(_sex)

    @field:Element(name = "size")
    private lateinit var _size: String
    open val size: AnimalSize
        get() = AnimalSize.fromString(_size)

    @field:ElementList(name = "options", entry = "option")
    private lateinit var _options: List<String>
    open val options: List<Option>
        get() = _options.map { Option.fromString(it) }

    @field:Element
    open lateinit var lastUpdate: String
        protected set

    @field:Element
    open lateinit var contact: Contact
        protected set

    @field:Element(required = false)
    open var shelterPetId: String? = null
        protected set

    @field:Element(required = false)
    open var description: String? = null
        protected set

    @field:Element(required = false)
    open var media: Media? = null
        protected set
}