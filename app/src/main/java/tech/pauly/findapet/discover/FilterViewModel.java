package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.ToggleButton;

import javax.inject.Inject;

import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

public class FilterViewModel extends BaseViewModel {

    public ObservableField<Sex> selectedSex = new ObservableField<>(Sex.MISSING);
    public ObservableField<Age> selectedAge = new ObservableField<>(Age.MISSING);
    public ObservableField<AnimalSize> selectedSize = new ObservableField<>(AnimalSize.MISSING);

    private FilterRepository filterRepository;
    private ViewEventBus eventBus;
    private TransientDataStore dataStore;

    @Inject
    FilterViewModel(FilterRepository filterRepository,
                    ViewEventBus eventBus,
                    TransientDataStore dataStore) {
        this.filterRepository = filterRepository;
        this.eventBus = eventBus;
        this.dataStore = dataStore;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void loadCurrentFilter() {
        subscribeOnLifecycle(filterRepository.getCurrentFilter()
                                             .subscribe(this::populateScreenForFilter, Throwable::printStackTrace));
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

    public void saveFilter(View v) {
        Filter filter = new Filter();
        filter.setSex(selectedSex.get());
        filter.setAge(selectedAge.get());
        filter.setSize(selectedSize.get());
        subscribeOnLifecycle(filterRepository.insertFilter(filter)
                                             .subscribe(this::finish, Throwable::printStackTrace));
    }

    public void clickBreedSearch(View v) {
        eventBus.send(ActivityEvent.build(this).startActivity(BreedActivity.class));
    }

    private void finish() {
        dataStore.save(new FilterUpdatedUseCase());
        eventBus.send(ActivityEvent.build(this).finishActivity());
    }

    private void populateScreenForFilter(Filter filter) {
        selectedSex.set(filter.getSex());
        selectedAge.set(filter.getAge());
        selectedSize.set(filter.getSize());
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
