package tech.pauly.findapet.shared;

import android.support.annotation.NonNull;
import android.widget.ToggleButton;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.favorites.FavoritesFragment;
import tech.pauly.findapet.settings.SettingsFragment;
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.datastore.UseCase;
import tech.pauly.findapet.shared.events.BaseViewEvent;
import tech.pauly.findapet.shared.events.FragmentEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;
import tech.pauly.findapet.shelters.SheltersFragment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainViewModelTest {

    @Mock
    private ViewEventBus eventBus;

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private DiscoverToolbarTitleUseCase useCase;

    private MainViewModel subject;
    private TestObserver<MainViewModel.ExpandingLayoutEvent> expandingLayoutObserver = new TestObserver<>();
    private TestObserver<Boolean> drawerCloseObserver = new TestObserver<>();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(useCase.getTitle()).thenReturn(R.string.tab_cat);
        when(dataStore.observeAndGetUseCase(any())).thenReturn(Observable.just(useCase));
        subject = new MainViewModel(eventBus, dataStore);
        subject.getExpandingLayoutSubject().subscribe(expandingLayoutObserver);
        subject.getDrawerSubject().subscribe(drawerCloseObserver);
    }

    @Test
    public void subscribeToDataStore_getToolbarTileUseCase_updateToolbarTitle() {
        subject.subscribeToDataStore();

        verify(dataStore).observeAndGetUseCase(DiscoverToolbarTitleUseCase.class);
        assertThat(subject.toolbarTitle.get()).isEqualTo(R.string.tab_cat);
    }

    @Test
    public void firstLaunch_launchesCatAnimalType() {
        subject.clickFirstAnimal();
        subject.subscribeToDataStore();

        verify(dataStore).save(new DiscoverAnimalTypeUseCase(AnimalType.CAT));
        verify(eventBus).send(new FragmentEvent(subject, DiscoverFragment.class, R.id.fragment_content));
    }

    @Test
    public void notFirstLaunch_doNotLaunchFragment() {
        subject.clickFirstAnimal();
        subject.subscribeToDataStore();
        clearInvocations(dataStore);
        clearInvocations(eventBus);

        subject.subscribeToDataStore();

        verify(dataStore, never()).save(any(UseCase.class));
        verify(eventBus, never()).send(any(BaseViewEvent.class));
    }

    @Test
    public void clickAnimalType_launchDiscoverFragmentForAnimalTypeAndSetsCurrentAnimalType() {
        subject.clickAnimalType(AnimalType.BARNYARD);

        assertThat(subject.currentAnimalType.get()).isEqualTo(AnimalType.BARNYARD);
        verify(dataStore).save(new DiscoverAnimalTypeUseCase(AnimalType.BARNYARD));
        verify(eventBus).send(new FragmentEvent(subject, DiscoverFragment.class, R.id.fragment_content));
        drawerCloseObserver.assertValue(true);
    }

    @Test
    public void clickStandardButton_buttonAlreadyChecked_stayChecked() {
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(false);

        subject.clickShelters(button);

        verify(button).setChecked(true);
        drawerCloseObserver.assertValue(true);
        assertThat(subject.currentAnimalType.get()).isNull();
    }

    @Test
    public void clickStandardButton_buttonNotChecked_collapseDiscoverViewAndSetCurrentButtonAndResetCurrentAnimal() {
        ToggleButton button = mockButton();

        subject.clickShelters(button);

        expandingLayoutObserver.assertValue(MainViewModel.ExpandingLayoutEvent.COLLAPSE);
        assertThat(subject.currentButton.get()).isEqualTo(1);
        verify(button, never()).setChecked(true);
        drawerCloseObserver.assertValue(true);
        assertThat(subject.currentAnimalType.get()).isNull();
    }

    @Test
    public void clickShelters_launchesSheltersFragment() {
        subject.clickShelters(mockButton());

        verify(eventBus).send(new FragmentEvent(subject, SheltersFragment.class, R.id.fragment_content));
    }

    @Test
    public void clickFavoritesButton_launchesFavoritesFragment() {
        subject.clickFavorites(mockButton());

        verify(eventBus).send(new FragmentEvent(subject, FavoritesFragment.class, R.id.fragment_content));
    }

    @Test
    public void clickSettingsButton_launchesSettingsFragment() {
        subject.clickSettings(mockButton());

        verify(eventBus).send(new FragmentEvent(subject, SettingsFragment.class, R.id.fragment_content));
    }

    @NonNull
    private ToggleButton mockButton() {
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(true);
        when(button.getId()).thenReturn(1);
        return button;
    }
}