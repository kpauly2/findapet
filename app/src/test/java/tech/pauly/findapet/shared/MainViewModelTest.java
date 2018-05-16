package tech.pauly.findapet.shared;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainViewModelTest {

    @Mock
    private ViewEventBus eventBus;

    @Mock
    private TransientDataStore dataStore;

    @Mock
    DiscoverToolbarTitleUseCase useCase;

    private MainViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(useCase.getTitle()).thenReturn(R.string.tab_cat);
        when(dataStore.observeAndGetUseCase(any())).thenReturn(Observable.just(useCase));
        subject = new MainViewModel(eventBus, dataStore);
    }

    @Test
    public void subscribeToDataStore_getToolbarTileUseCase_updateToolbarTitle() {
        subject.subscribeToDataStore();

        verify(dataStore).observeAndGetUseCase(DiscoverToolbarTitleUseCase.class);
        assertThat(subject.toolbarTitle.get()).isEqualTo(R.string.tab_cat);
    }

    @Test
    public void temp_sendDiscoverFragmentEvent() {
        subject.temp();

        verify(eventBus).send(FragmentEvent.build(subject)
                                           .container(R.id.fragment_content)
                                           .fragment(DiscoverFragment.class));
    }
}