package tech.pauly.findapet.discover;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.Media;
import tech.pauly.findapet.data.models.Option;
import tech.pauly.findapet.data.models.Photo;
import tech.pauly.findapet.data.models.PhotoSize;
import tech.pauly.findapet.shared.AnimalDetailsUseCase;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.ResourceProvider;
import tech.pauly.findapet.shared.TransientDataStore;

public class AnimalDetailsViewModel extends BaseViewModel {

    private final ResourceProvider resourceProvider;
    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> imageUrl = new ObservableField<>("");
    public ObservableField<String> ageType = new ObservableField<>("");
    public ObservableField<String> breeds = new ObservableField<>("");
    public ObservableInt sex = new ObservableInt(R.string.missing);
    public ObservableInt size = new ObservableInt(R.string.missing);
    public ObservableField<String> options = new ObservableField<>("");
    public ObservableField<String> description = new ObservableField<>("");
    public ObservableBoolean descriptionVisibility = new ObservableBoolean(false);
    public ObservableBoolean optionsVisibility = new ObservableBoolean(false);

    private AnimalDetailsViewPagerAdapter viewPagerAdapter;

    @Inject
    AnimalDetailsViewModel(TransientDataStore dataStore,
                           AnimalDetailsViewPagerAdapter viewPagerAdapter,
                           ResourceProvider resourceProvider) {
        this.viewPagerAdapter = viewPagerAdapter;
        this.resourceProvider = resourceProvider;
        this.viewPagerAdapter.setViewModel(this);
        AnimalDetailsUseCase useCase = dataStore.get(AnimalDetailsUseCase.class);
        if (useCase != null) {
            Animal animal = useCase.getAnimal();
            name.set(animal.getName());
            sex.set(animal.getSex().getFormattedName());
            size.set(animal.getSize().getFormattedName());
            ageType.set(animal.getAge() + " " + resourceProvider.getString(animal.getType().getSingularName()));

            setPhoto(animal.getMedia());
            setBreeds(animal.getBreedList());
            setOptions(animal.getOptions());
            setDescription(animal.getDescription());
        }
    }

    public AnimalDetailsViewPagerAdapter getViewPagerAdapter() {
        return viewPagerAdapter;
    }

    private void setDescription(String description) {
        if (description != null && !description.isEmpty()) {
            descriptionVisibility.set(true);
            this.description.set(description);
        }
    }

    private void setOptions(List<Option> options) {
        if (options.size() > 0) {
            optionsVisibility.set(true);
            StringBuilder optionsText = new StringBuilder();
            for (int i = 0; i < options.size(); i++) {
                Option option = options.get(i);
                optionsText.append(resourceProvider.getString(option.getFormattedName()));
                if (i < options.size() - 1) {
                    optionsText.append("\n");
                }
            }
            this.options.set(optionsText.toString());
        }
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
