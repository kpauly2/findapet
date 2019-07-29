package tech.pauly.old.discover

import androidx.databinding.ObservableField
import tech.pauly.old.data.models.AnimalUrl

import tech.pauly.old.shared.BaseViewModel

class AnimalImageViewModel(url: AnimalUrl) : BaseViewModel() {

    var imageUrl = ObservableField<AnimalUrl>()

    init {
        imageUrl.set(url)
    }
}
