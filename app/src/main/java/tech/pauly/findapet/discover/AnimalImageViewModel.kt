package tech.pauly.findapet.discover

import android.databinding.ObservableField

import tech.pauly.findapet.data.models.Photo
import tech.pauly.findapet.shared.BaseViewModel

class AnimalImageViewModel(url: String) : BaseViewModel() {

    var imageUrl = ObservableField("")

    init {
        imageUrl.set(url)
    }
}
