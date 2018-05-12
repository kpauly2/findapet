package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.pauly.findapet.data.models.AnimalType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscoverViewModelTest {

    @Mock
    private AnimalTypeViewPagerAdapter viewPagerAdapter;

    @Mock
    private AnimalTypeListViewModel.Factory animalTypeListFactory;

    @Mock
    private AnimalTypeListViewModel animalTypeListViewModel;

    private DiscoverViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(animalTypeListFactory.newInstance(any(AnimalType.class))).thenReturn(animalTypeListViewModel);
        subject = new DiscoverViewModel(viewPagerAdapter, animalTypeListFactory);
    }

    @Test
    public void onCreate_setsUpViewModelsForEachAnimalTypeList() {
        verify(animalTypeListFactory, times(AnimalType.values().length)).newInstance(any(AnimalType.class));
    }

    @Test
    public void onPageChange_callsPageChangeOnChildViewModel() {
        subject.onPageChange(0);

        verify(animalTypeListViewModel).onPageChange();
    }
}