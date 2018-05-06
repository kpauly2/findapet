package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.Media;
import tech.pauly.findapet.data.models.Option;
import tech.pauly.findapet.data.models.Photo;
import tech.pauly.findapet.data.models.PhotoSize;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.AnimalDetailsUseCase;
import tech.pauly.findapet.shared.ResourceProvider;
import tech.pauly.findapet.shared.TransientDataStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnimalDetailsViewModelTest {

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private AnimalDetailsViewPagerAdapter viewPagerAdapter;

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
        assertThat(subject.ageType.get()).isEqualTo("Adult Cat");
    }

    @Test
    public void onCreate_noUseCaseReceived_fieldsShowDefaultValues() {
        createSubjectWithUseCase(null);

        assertThat(subject.name.get()).isEqualTo("");
        assertThat(subject.imageUrl.get()).isEqualTo("");
        assertThat(subject.ageType.get()).isEqualTo("");
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
    public void onCreate_animalPhotoPresent_setImageUrl() {
        createSubjectWithUseCase(setupFullAnimalUseCase());

        assertThat(subject.imageUrl.get()).isEqualTo("http://url.com");
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

    private AnimalDetailsUseCase setupFullAnimalUseCase() {
        Photo photo = mock(Photo.class);
        when(photo.getUrl()).thenReturn("http://url.com");
        when(photo.getSize()).thenReturn(PhotoSize.x);

        Media media = mock(Media.class);
        when(media.getPhotoList()).thenReturn(Collections.singletonList(photo));

        Animal animal = mock(Animal.class);
        when(animal.getName()).thenReturn("name");
        when(animal.getSex()).thenReturn(Sex.M);
        when(animal.getSize()).thenReturn(AnimalSize.L);
        when(animal.getType()).thenReturn(AnimalType.Cat);
        when(animal.getAge()).thenReturn(Age.Adult);
        when(animal.getDescription()).thenReturn("");
        when(animal.getMedia()).thenReturn(media);
        when(animal.getBreedList()).thenReturn(Arrays.asList("breed1", "breed2"));
        when(animal.getOptions()).thenReturn(Arrays.asList(Option.altered, Option.houseBroken));
        when(animal.getDescription()).thenReturn("description");

        AnimalDetailsUseCase useCase = mock(AnimalDetailsUseCase.class);
        when(useCase.getAnimal()).thenReturn(animal);

        return useCase;
    }
    private void createSubjectWithUseCase(AnimalDetailsUseCase useCase) {
        when(dataStore.get(AnimalDetailsUseCase.class)).thenReturn(useCase);
        when(resourceProvider.getString(R.string.cat)).thenReturn("Cat");
        when(resourceProvider.getString(R.string.altered)).thenReturn("Altered");
        when(resourceProvider.getString(R.string.house_broken)).thenReturn("House Broken");
        subject = new AnimalDetailsViewModel(dataStore, viewPagerAdapter, resourceProvider);
    }
}