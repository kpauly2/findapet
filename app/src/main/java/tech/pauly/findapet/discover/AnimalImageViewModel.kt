package tech.pauly.findapet.discover

import android.databinding.ObservableField
import tech.pauly.findapet.data.models.AnimalUrl

import tech.pauly.findapet.shared.BaseViewModel

class AnimalImageViewModel(url: AnimalUrl) : BaseViewModel() {

    var imageUrl = ObservableField<AnimalUrl>()

    init {
        imageUrl.set(url)
    }
}
