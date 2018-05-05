package tech.pauly.findapet.discover;

import android.databinding.ObservableField;

import java.util.List;

import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.Media;
import tech.pauly.findapet.data.models.Photo;
import tech.pauly.findapet.data.models.PhotoSize;

public class AnimalListItemViewModel {

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> imageUrl = new ObservableField<>("");
    public ObservableField<String> age = new ObservableField<>("");
    public ObservableField<String> breeds = new ObservableField<>("");

    public AnimalListItemViewModel(Animal animal) {
        name.set(animal.getName());
        age.set(animal.getAge().toString());

        setBreeds(animal.getBreedList());
        setPhoto(animal.getMedia());
    }

    private void setBreeds(List<String> breedList) {
        StringBuilder breedString = new StringBuilder();
        for (int i = 0; i < breedList.size(); i++) {
            String currentBreed = breedList.get(i);
            breedString.append(currentBreed);
            if (i != breedList.size() - 1) {
                breedString.append(" / ");
            }
        }
        this.breeds.set(breedString.toString());
    }

    private void setPhoto(Media media) {
        if (media != null && media.getPhotoList() != null && !media.getPhotoList().isEmpty()) {
            Photo finalPhoto = null;
            // TODO: Fallback sizes: https://www.pivotaltracker.com/story/show/157261497
            for (Photo photo : media.getPhotoList()) {
                if (photo.getSize() == PhotoSize.x) {
                    finalPhoto = photo;
                }
            }
            if (finalPhoto != null) {
                imageUrl.set(finalPhoto.getUrl());
            }
        }
    }
}
