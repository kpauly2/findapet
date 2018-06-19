package tech.pauly.findapet.discover;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.Contact;
import tech.pauly.findapet.data.models.Media;
import tech.pauly.findapet.data.models.Photo;
import tech.pauly.findapet.data.models.PhotoSize;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.LocationHelper;
import tech.pauly.findapet.shared.ResourceProvider;
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

public class AnimalListItemViewModel extends BaseViewModel {

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> imageUrl = new ObservableField<>("");
    public ObservableField<String> age = new ObservableField<>("");
    public ObservableField<String> breeds = new ObservableField<>("");
    public ObservableField<String> distance = new ObservableField<>("?");
    public ObservableBoolean distanceVisibility = new ObservableBoolean(false);

    private Animal animal;
    private ViewEventBus eventBus;
    private TransientDataStore dataStore;
    private LocationHelper locationHelper;

    protected AnimalListItemViewModel(Animal animal,
                                      ViewEventBus eventBus,
                                      TransientDataStore dataStore,
                                      ResourceProvider resourceProvider,
                                      LocationHelper locationHelper) {
        this.animal = animal;
        this.eventBus = eventBus;
        this.dataStore = dataStore;
        this.locationHelper = locationHelper;
        populateFields(animal, resourceProvider);
    }

    public void launchAnimalDetails() {
        dataStore.save(new AnimalDetailsUseCase(animal));
        eventBus.send(new ActivityEvent(this, AnimalDetailsActivity.class, false));
    }

    private void populateFields(Animal animal, ResourceProvider resourceProvider) {
        name.set(animal.getName());
        age.set(resourceProvider.getString(animal.getAge().getFormattedName()));

        setBreeds(animal.getBreedList());
        setPhoto(animal.getMedia());
        setDistance(animal.getContact());
    }

    private void setDistance(Contact contactInfo) {
        onLifecycle(locationHelper.getCurrentDistanceToContactInfo(contactInfo)
                                  .subscribe(this::displayDistance, Throwable::printStackTrace));
    }

    private void displayDistance(Integer returnDistance) {
        if (returnDistance == -1) {
            distanceVisibility.set(false);
        } else {
            distanceVisibility.set(true);
            String distanceString = returnDistance.toString();
            if (returnDistance == 0) {
                distanceString = "< 1";
            }
            distance.set(distanceString);
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
            for (Photo photo : media.getPhotoList()) {
                if (photo.getSize() == PhotoSize.LARGE) {
                    finalPhoto = photo;
                    break;
                }
            }
            if (finalPhoto != null) {
                imageUrl.set(finalPhoto.getUrl());
            }
        }
    }

    public static class Factory {
        private ViewEventBus eventBus;
        private TransientDataStore dataStore;
        private ResourceProvider resourceProvider;
        private LocationHelper locationHelper;

        @Inject
        public Factory(ViewEventBus eventBus, TransientDataStore dataStore, ResourceProvider resourceProvider, LocationHelper locationHelper) {
            this.eventBus = eventBus;
            this.dataStore = dataStore;
            this.resourceProvider = resourceProvider;
            this.locationHelper = locationHelper;
        }

        public AnimalListItemViewModel newInstance(Animal animal) {
            return new AnimalListItemViewModel(animal, eventBus, dataStore, resourceProvider, locationHelper);
        }
    }
}
