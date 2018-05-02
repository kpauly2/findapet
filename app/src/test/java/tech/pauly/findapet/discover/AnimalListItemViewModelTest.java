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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnimalListItemViewModelTest {

    @Mock
    Animal animal;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(animal.getName()).thenReturn("name");
        when(animal.getAge()).thenReturn(Age.Adult);
        when(animal.getBreedList()).thenReturn(Collections.singletonList("breeds"));
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.emptyList());
        when(animal.getMedia()).thenReturn(media);
    }

    @Test
    public void onCreate_setAllBasicValuesForInputAnimal() {
        AnimalListItemViewModel subject = new AnimalListItemViewModel(animal);

        assertThat(subject.name.get()).isEqualTo("name");
        assertThat(subject.age.get()).isEqualTo("Adult");
        assertThat(subject.breeds.get()).isEqualTo("breeds");
    }

    @Test
    public void onCreate_animalHasMultipleBreeds_setsAllBreeds() {
        when(animal.getBreedList()).thenReturn(Arrays.asList("breed1", "breed2"));

        AnimalListItemViewModel subject = new AnimalListItemViewModel(animal);

        assertThat(subject.breeds.get()).isEqualTo("breed1 / breed2");
    }

    @Test
    public void setPhoto_animalHasXPhoto_setsPhoto() {
        Photo photo = mock(Photo.class);
        when(photo.getUrl()).thenReturn("http://url.com");
        when(photo.getSize()).thenReturn(PhotoSize.x);
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.singletonList(photo));
        when(animal.getMedia()).thenReturn(media);

        AnimalListItemViewModel subject = new AnimalListItemViewModel(animal);

        assertThat(subject.imageUrl.get()).isEqualTo("http://url.com");
    }

    @Test
    public void setPhoto_animalHasNoPhotos_doNotSetPhoto() {
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.emptyList());
        when(animal.getMedia()).thenReturn(media);

        AnimalListItemViewModel subject = new AnimalListItemViewModel(animal);

        assertThat(subject.imageUrl.get()).isEqualTo("");
    }

    @Test
    public void setPhoto_animalDoesNotHaveXPhoto_doNotSetPhoto() {
        Photo photo = mock(Photo.class);
        when(photo.getUrl()).thenReturn("http://url.com");
        when(photo.getSize()).thenReturn(PhotoSize.fpm);
        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.singletonList(photo));
        when(animal.getMedia()).thenReturn(media);

        AnimalListItemViewModel subject = new AnimalListItemViewModel(animal);

        assertThat(subject.imageUrl.get()).isEqualTo("");
    }
}