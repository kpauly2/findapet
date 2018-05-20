package tech.pauly.findapet.discover;

import android.databinding.ObservableField;

import tech.pauly.findapet.data.models.Photo;
import tech.pauly.findapet.shared.BaseViewModel;

public class AnimalImageViewModel extends BaseViewModel {
    public ObservableField<String> imageUrl = new ObservableField<>("");

    public AnimalImageViewModel(Photo photo) {
        imageUrl.set(photo.getUrl());
    }
}
