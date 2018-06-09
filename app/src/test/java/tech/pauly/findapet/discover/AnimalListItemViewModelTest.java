package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.Media;
import tech.pauly.findapet.data.models.Photo;
import tech.pauly.findapet.data.models.PhotoSize;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.ResourceProvider;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ViewEventBus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnimalListItemViewModelTest {

    @Mock
    private Animal animal;

    @Mock
    private ViewEventBus eventBus;

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private ResourceProvider resourceProvider;

    private AnimalListItemViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(animal.getName()).thenReturn("name");
        when(animal.getAge()).thenReturn(Age.ADULT);
        when(animal.getBreedList()).thenReturn(Collections.singletonList("breeds"));
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.emptyList());
        when(animal.getMedia()).thenReturn(media);
        when(resourceProvider.getString(Age.ADULT.getFormattedName())).thenReturn("Adult");
    }

    @Test
    public void onCreate_setAllBasicValuesForInputAnimal() {
        createSubject();

        assertThat(subject.name.get()).isEqualTo("name");
        assertThat(subject.age.get()).isEqualTo("Adult");
        assertThat(subject.breeds.get()).isEqualTo("breeds");
    }

    @Test
    public void onCreate_animalHasMultipleBreeds_setsAllBreeds() {
        when(animal.getBreedList()).thenReturn(Arrays.asList("breed1", "breed2"));

        createSubject();

        assertThat(subject.breeds.get()).isEqualTo("breed1 / breed2");
    }

    @Test
    public void onCreate_distanceVisibilityFalse() {
        createSubject();
        
        assertThat(subject.getDistanceVisibility()).isFalse();
    }

    @Test
    public void setPhoto_animalHasXPhoto_setsPhoto() {
        Photo photo = mock(Photo.class);
        when(photo.getUrl()).thenReturn("http://url.com");
        when(photo.getSize()).thenReturn(PhotoSize.LARGE);
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.singletonList(photo));
        when(animal.getMedia()).thenReturn(media);

        createSubject();

        assertThat(subject.imageUrl.get()).isEqualTo("http://url.com");
    }

    @Test
    public void setPhoto_animalHasNoPhotos_doNotSetPhoto() {
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.emptyList());
        when(animal.getMedia()).thenReturn(media);

        createSubject();

        assertThat(subject.imageUrl.get()).isEqualTo("");
    }

    @Test
    public void setPhoto_animalHasNullPhotos_doNotSetPhoto() {
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(null);
        when(animal.getMedia()).thenReturn(media);

        createSubject();

        assertThat(subject.imageUrl.get()).isEqualTo("");
    }

    @Test
    public void setPhoto_animalDoesNotHaveXPhoto_doNotSetPhoto() {
        Photo photo = mock(Photo.class);
        when(photo.getUrl()).thenReturn("http://url.com");
        when(photo.getSize()).thenReturn(PhotoSize.FEATURED_PET_MODULE);
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.singletonList(photo));
        when(animal.getMedia()).thenReturn(media);

        createSubject();

        assertThat(subject.imageUrl.get()).isEqualTo("");
    }

    @Test
    public void setPhoto_mediaIsNull_doNotSetPhoto() {
        when(animal.getMedia()).thenReturn(null);

        createSubject();

        assertThat(subject.imageUrl.get()).isEqualTo("");
    }

    @Test
    public void launchAnimalDetails_launchesAnimalDetails() {
        createSubject();

        subject.launchAnimalDetails();

        verify(eventBus).send(ActivityEvent.build(subject).startActivity(AnimalDetailsActivity.class));
    }

    @Test
    public void distanceLessThanZero_distanceVisibilityFalse() {
        createSubject();

        subject.distance.set(-1);

        assertThat(subject.getDistanceVisibility()).isFalse();
    }

    @Test
    public void distanceZero_distanceVisibilityTrue() {
        createSubject();

        subject.distance.set(0);

        assertThat(subject.getDistanceVisibility()).isTrue();
    }

    @Test
    public void distanceLessMoreThanZero_distanceVisibilityTrue() {
        createSubject();

        subject.distance.set(1);

        assertThat(subject.getDistanceVisibility()).isTrue();
    }

    private void createSubject() {
        subject = new AnimalListItemViewModel(animal, eventBus, dataStore, resourceProvider);
    }
}