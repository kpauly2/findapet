package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.ToggleButton;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import tech.pauly.findapet.data.BreedRepository;
import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.BreedListResponse;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

public class FilterViewModel extends BaseViewModel {

    public ObservableField<Sex> selectedSex = new ObservableField<>(Sex.MISSING);
    public ObservableField<Age> selectedAge = new ObservableField<>(Age.MISSING);
    public ObservableField<AnimalSize> selectedSize = new ObservableField<>(AnimalSize.MISSING);
    public ObservableField<String> selectedBreed = new ObservableField<>("");

    private FilterRepository filterRepository;
    private BreedRepository breedRepository;
    private ViewEventBus eventBus;
    private TransientDataStore dataStore;
    private FilterAdapter adapter;
    private PublishSubject<View> scrollToViewSubject = PublishSubject.create();

    @Inject
    FilterViewModel(FilterRepository filterRepository,
                    BreedRepository breedRepository,
                    ViewEventBus eventBus,
                    TransientDataStore dataStore,
                    FilterAdapter adapter) {
        this.filterRepository = filterRepository;
        this.breedRepository = breedRepository;
        this.eventBus = eventBus;
        this.dataStore = dataStore;
        this.adapter = adapter;
        adapter.setViewModel(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void loadCurrentFilter() {
        subscribeOnLifecycle(filterRepository.getCurrentFilter()
                                             .subscribe(this::populateScreenForFilter, Throwable::printStackTrace));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void updateBreedList() {
        FilterAnimalTypeUseCase useCase = dataStore.get(FilterAnimalTypeUseCase.class);
        if (useCase != null) {
            subscribeOnLifecycle(breedRepository.getBreedList(useCase.getAnimalType())
                                                .subscribe(this::populateBreedList, Throwable::printStackTrace));
        }
    }

    public void clickBreedSearch(View view) {
        scrollToViewSubject.onNext(view);
    }

    public void checkSex(View view, Sex sex) {
        selectedSex.set(isViewChecked(view) ? sex : Sex.MISSING);
    }
    public void checkAge(View view, Age age) {
        selectedAge.set(isViewChecked(view) ? age : Age.MISSING);
    }

    public void checkSize(View view, AnimalSize size) {
        selectedSize.set(isViewChecked(view) ? size : AnimalSize.MISSING);
    }

    public void checkBreed(View view, String breed) {
        selectedBreed.set(isViewChecked(view) ? breed : "");
    }

    public void saveFilter(View v) {
        Filter filter = new Filter();
        filter.setSex(selectedSex.get());
        filter.setAge(selectedAge.get());
        filter.setSize(selectedSize.get());
        filter.setBreed(selectedBreed.get());
        subscribeOnLifecycle(filterRepository.insertFilter(filter)
                                             .subscribe(this::finish, Throwable::printStackTrace));
    }

    public FilterAdapter getAdapter() {
        return adapter;
    }

    public Observable<View> getScrollToViewSubject() {
        return scrollToViewSubject;
    }

    private void finish() {
        dataStore.save(new FilterUpdatedUseCase());
        eventBus.send(ActivityEvent.build(this).finishActivity());
    }

    private void populateScreenForFilter(Filter filter) {
        selectedSex.set(filter.getSex());
        selectedAge.set(filter.getAge());
        selectedSize.set(filter.getSize());
        selectedBreed.set(filter.getBreed());
    }

    private void populateBreedList(BreedListResponse response) {
        List<String> breedList = response.getBreedList();
        if (breedList != null) {
            adapter.setBreedItems(breedList);
        }
    }

    private boolean isViewChecked(View view) {
        boolean checked = false;
        if (view instanceof ToggleButton) {
            ToggleButton button = (ToggleButton) view;
            checked = button.isChecked();
        }
        return checked;
    }
}
