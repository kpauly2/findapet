package tech.pauly.findapet.data.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import io.reactivex.Single
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import tech.pauly.findapet.shared.ResourceProvider

private const val filenameSuffix = ".png"

sealed class Animal {

    @field:Element
    open var id: Int = 0

    @field:Element
    open lateinit var shelterId: String

    @field:Element
    open lateinit var name: String
        internal set

    @field:Element(name = "animal")
    internal lateinit var _type: String
    open val type: AnimalType
        get() = AnimalType.fromString(_type)

    @Ignore
    @field:ElementList(name = "breeds", entry = "breed")
    open lateinit var breedList: List<String>
        internal set

    @field:Element
    open lateinit var mix: String
        internal set

    @field:Element(name = "age")
    internal lateinit var _age: String
    open val age: Age
        get() = Age.fromString(_age)

    @field:Element(name = "sex")
    internal lateinit var _sex: String
    open val sex: Sex
        get() = Sex.fromString(_sex)

    @field:Element(name = "size")
    internal lateinit var _size: String
    open val size: AnimalSize
        get() = AnimalSize.fromString(_size)

    @field:ElementList(name = "options", entry = "option")
    internal lateinit var _options: List<String>
    open val options: List<Option>
        get() = _options.map { Option.fromString(it) }

    @Ignore
    @field:Element
    open lateinit var contact: Shelter
        internal set

    @field:Element(required = false)
    open var shelterPetId: String? = null

    @field:Element(required = false)
    open var description: String? = null

    abstract val formattedBreedList: String
    abstract val primaryPhotoUrl: String?
    abstract val photoUrlList: List<String>?
    abstract fun deleteLocalPhotos(resourceProvider: ResourceProvider)
}

@Root(name = "pet", strict = false)
open class InternetAnimal : Animal() {
    @field:Element(required = false)
    open var media: Media? = null
        internal set

    @field:Element
    open lateinit var lastUpdate: String
        internal set

    override val formattedBreedList: String
        get() = breedList.joinToString(" / ")

    override val primaryPhotoUrl: String?
        get() = photoUrlList?.get(0)

    override fun deleteLocalPhotos(resourceProvider: ResourceProvider) {
        photoUrlList?.mapIndexed { index, _ ->
            val filename = "${this.id}-$index$filenameSuffix"
            resourceProvider.deleteFile(filename)
        }
    }

    override val photoUrlList: List<String>?
        get() = media?.photoList?.mapNotNull { photo ->
            photo.url.takeIf { photo.size == PhotoSize.LARGE }
        }.also {
            if (it?.isEmpty() != false) return null
        }
}

@Entity
open class LocalAnimal(@PrimaryKey(autoGenerate = true)
                       open var dbId: Long? = null) : Animal() {

    open var photoList: List<String>? = null

    override val photoUrlList: List<String>?
        get() = photoList

    override val primaryPhotoUrl: String?
        get() = photoList?.get(0)

    internal lateinit var _formattedBreedList: String
    override val formattedBreedList: String
        get() = _formattedBreedList

    override fun deleteLocalPhotos(resourceProvider: ResourceProvider) {
        photoList?.forEach {
            resourceProvider.deleteFile(it)
        }
    }

    open fun fromInternetAnimal(other: InternetAnimal, resourceProvider: ResourceProvider): Single<LocalAnimal> {
        this.id = other.id
        this.shelterId = other.shelterId
        this.name = other.name
        this._type = other._type
        this._formattedBreedList = other.formattedBreedList
        this.mix = other.mix
        this._age = other._age
        this._sex = other._sex
        this._size = other._size
        this._options = other._options
        this.contact = other.contact
        this.contact.id = other.shelterId
        this.shelterPetId = other.shelterPetId
        this.description = other.description

        return Single.fromCallable { downloadPhotos(other, resourceProvider) }
                .map { this }
    }

    private fun downloadPhotos(other: InternetAnimal, resourceProvider: ResourceProvider): Boolean {
        return other.photoUrlList?.mapIndexed { index, url ->
            val filename = "$id-$index$filenameSuffix"
            val bitmap = resourceProvider.getBitmapFromUrl(url)
            resourceProvider.saveBitmapToFile(filename, bitmap)
        }?.let {
            photoList = it
            true
        } ?: false
    }
}