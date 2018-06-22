package tech.pauly.findapet.discover

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.Media
import tech.pauly.findapet.data.models.Option
import tech.pauly.findapet.data.models.PhotoSize
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import javax.inject.Inject

class AnimalDetailsViewModel @Inject
internal constructor(dataStore: TransientDataStore,
                     val detailsPagerAdapter: AnimalDetailsViewPagerAdapter,
                     val imagesPagerAdapter: AnimalImagesPagerAdapter,
                     private val resourceProvider: ResourceProvider) : BaseViewModel() {

    var name = ObservableField("")
    var age = ObservableInt(R.string.missing)
    var breeds = ObservableField("")
    var sex = ObservableInt(R.string.missing)
    var size = ObservableInt(R.string.missing)
    var options = ObservableField("")
    var description = ObservableField("")
    var descriptionVisibility = ObservableBoolean(false)
    var optionsVisibility = ObservableBoolean(false)
    var imagesPageLimit = ObservableInt(4)
    var imagesCount = ObservableInt(0)
    var currentImagePosition = ObservableInt(0)

    init {
        this.detailsPagerAdapter.setViewModel(this)

        dataStore[AnimalDetailsUseCase::class]?.let {
            val animal = it.animal
            name.set(animal.name)
            sex.set(animal.sex.formattedName)
            size.set(animal.size.formattedName)
            age.set(animal.age.formattedName)
            breeds.set(animal.formattedBreedList)

            setPhotos(animal.media)
            setOptions(animal.options)
            setDescription(animal.description)
        }
    }

    fun imagePageChange(position: Int) {
        currentImagePosition.set(position)
    }

    private fun setDescription(description: String?) {
        if (!description.isNullOrBlank()) {
            descriptionVisibility.set(true)
            this.description.set(description)
        }
    }

    private fun setOptions(options: List<Option>) {
        if (options.isNotEmpty()) {
            optionsVisibility.set(true)
            this.options.set(options.joinToString("\n") {
                resourceProvider.getString(it.formattedName)
            })
        }
    }

    private fun setPhotos(media: Media?) {
        media?.photoList?.mapNotNull { photo ->
            AnimalImageViewModel(photo).takeIf {
                photo.size == PhotoSize.LARGE
            }
        }?.also {
            imagesCount.set(it.size)
            imagesPagerAdapter.setAnimalImages(it)
        }
    }
}
