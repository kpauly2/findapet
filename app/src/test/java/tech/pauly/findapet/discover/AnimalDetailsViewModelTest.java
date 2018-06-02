package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.Media;
import tech.pauly.findapet.data.models.Option;
import tech.pauly.findapet.data.models.Photo;
import tech.pauly.findapet.data.models.PhotoSize;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.ResourceProvider;
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnimalDetailsViewModelTest {

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private AnimalDetailsViewPagerAdapter viewPagerAdapter;

    @Mock
    private AnimalImagesPagerAdapter imagesPagerAdapter;

    @Mock
    private ResourceProvider resourceProvider;

    private AnimalDetailsViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onCreate_getAnimalFromUseCase_setAllBasicValues() {
        createSubjectWithUseCase(setupFullAnimalUseCase());

        assertThat(subject.name.get()).isEqualTo("name");
        assertThat(subject.sex.get()).isEqualTo(R.string.male);
        assertThat(subject.size.get()).isEqualTo(R.string.large);
        assertThat(subject.age.get()).isEqualTo(R.string.adult);
    }

    @Test
    public void onCreate_noUseCaseReceived_fieldsShowDefaultValues() {
        createSubjectWithUseCase(null);

        assertThat(subject.name.get()).isEqualTo("");
        assertThat(subject.age.get()).isEqualTo(R.string.missing);
        assertThat(subject.breeds.get()).isEqualTo("");
        assertThat(subject.sex.get()).isEqualTo(R.string.missing);
        assertThat(subject.size.get()).isEqualTo(R.string.missing);
        assertThat(subject.options.get()).isEqualTo("");
        assertThat(subject.description.get()).isEqualTo("");
        assertThat(subject.descriptionVisibility.get()).isEqualTo(false);
        assertThat(subject.optionsVisibility.get()).isEqualTo(false);
    }

    @Test
    public void onCreate_animalDescriptionFieldIsNotNull_setDescriptionAndVisibility() {
        createSubjectWithUseCase(setupFullAnimalUseCase());

        assertThat(subject.descriptionVisibility.get()).isEqualTo(true);
        assertThat(subject.description.get()).isEqualTo("description");
    }

    @Test
    public void onCreate_animalDescriptionFieldIsEmpty_setDescriptionAndVisibility() {
        AnimalDetailsUseCase useCase = setupFullAnimalUseCase();
        when(useCase.getAnimal().getDescription()).thenReturn("");

        createSubjectWithUseCase(useCase);

        assertThat(subject.descriptionVisibility.get()).isEqualTo(false);
    }

    @Test
    public void onCreate_animalPhotoPresent_addsImagesToPagerAdapter() {
        createSubjectWithUseCase(setupFullAnimalUseCase());
        ArgumentCaptor<List<AnimalImageViewModel>> imageViewModelsCaptor = ArgumentCaptor.forClass(List.class);

        verify(imagesPagerAdapter).setAnimalImages(imageViewModelsCaptor.capture());
        List<AnimalImageViewModel> savedImageList = imageViewModelsCaptor.getValue();
        assertThat(savedImageList).hasSize(1);
        assertThat(savedImageList.get(0).imageUrl.get()).isEqualTo("http://url.com");
        assertThat(subject.imagesCount.get()).isEqualTo(1);
    }

    @Test
    public void onCreate_largeAnimalPhotoNotPresent_addsNoImagesToAdapter() {
        AnimalDetailsUseCase useCase = setupFullAnimalUseCase();
        when(useCase.getAnimal().getMedia()).thenReturn(null);
        createSubjectWithUseCase(useCase);

        verify(imagesPagerAdapter, never()).setAnimalImages(any(List.class));
        assertThat(subject.imagesCount.get()).isEqualTo(0);
    }

    @Test
    public void onCreate_animalHasMultipleBreeds_setsAllBreeds() {
        createSubjectWithUseCase(setupFullAnimalUseCase());

        assertThat(subject.breeds.get()).isEqualTo("breed1 / breed2");
    }

    @Test
    public void onCreate_animalHasNoOptions_optionsNotVisible() {
        AnimalDetailsUseCase useCase = setupFullAnimalUseCase();
        when(useCase.getAnimal().getOptions()).thenReturn(Collections.emptyList());

        createSubjectWithUseCase(useCase);

        assertThat(subject.optionsVisibility.get()).isEqualTo(false);
    }

    @Test
    public void onCreate_animalHasOptions_optionsListShown() {
        createSubjectWithUseCase(setupFullAnimalUseCase());

        assertThat(subject.optionsVisibility.get()).isEqualTo(true);
        assertThat(subject.options.get()).isEqualTo("Altered\nHouse Broken");
    }

    @Test
    public void imagePageChange_updateCurrentImagePosition() {
        createSubjectWithUseCase(setupFullAnimalUseCase());

        subject.imagePageChange(1);

        assertThat(subject.currentImagePosition.get()).isEqualTo(1);
    }

    private AnimalDetailsUseCase setupFullAnimalUseCase() {
        Photo photo = mock(Photo.class);
        when(photo.getUrl()).thenReturn("http://url.com");
        when(photo.getSize()).thenReturn(PhotoSize.LARGE);

        Photo photo2 = mock(Photo.class);
        when(photo2.getUrl()).thenReturn("http://url2.com");
        when(photo2.getSize()).thenReturn(PhotoSize.PET_NOTE_THUMBNAIL);

        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Arrays.asList(photo, photo2));

        Animal animal = mock(Animal.class);
        when(animal.getName()).thenReturn("name");
        when(animal.getSex()).thenReturn(Sex.M);
        when(animal.getSize()).thenReturn(AnimalSize.L);
        when(animal.getAge()).thenReturn(Age.ADULT);
        when(animal.getDescription()).thenReturn("");
        when(animal.getMedia()).thenReturn(media);
        when(animal.getBreedList()).thenReturn(Arrays.asList("breed1", "breed2"));
        when(animal.getOptions()).thenReturn(Arrays.asList(Option.ALTERED, Option.HOUSE_BROKEN));
        when(animal.getDescription()).thenReturn("description");

        AnimalDetailsUseCase useCase = mock(AnimalDetailsUseCase.class);
        when(useCase.getAnimal()).thenReturn(animal);

        return useCase;
    }
    private void createSubjectWithUseCase(AnimalDetailsUseCase useCase) {
        when(dataStore.get(AnimalDetailsUseCase.class)).thenReturn(useCase);
        when(resourceProvider.getString(R.string.altered)).thenReturn("Altered");
        when(resourceProvider.getString(R.string.house_broken)).thenReturn("House Broken");
        when(resourceProvider.getString(Age.ADULT.getName())).thenReturn("Adult");
        subject = new AnimalDetailsViewModel(dataStore, viewPagerAdapter, resourceProvider, imagesPagerAdapter);
    }
}