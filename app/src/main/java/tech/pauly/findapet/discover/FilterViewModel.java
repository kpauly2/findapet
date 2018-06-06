package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import javax.inject.Inject;

import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

import static tech.pauly.findapet.data.models.Sex.U;

public class FilterViewModel extends BaseViewModel {

    public ObservableField<Sex> selectedSex = new ObservableField<>(Sex.U);
    public ObservableField<Age> selectedAge = new ObservableField<>(Age.MISSING);

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
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            selectedSex.set(sex);
        } else {
            selectedSex.set(Sex.U);
        }
    }

    public void checkAge(View view, Age age) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            selectedAge.set(age);
        } else {
            selectedAge.set(Age.MISSING);
        }
    }

    public void saveFilter(View v) {
        Filter filter = new Filter();
        filter.setSex(selectedSex.get());
        filter.setAge(selectedAge.get());
        subscribeOnLifecycle(filterRepository.insertFilter(filter)
                                             .subscribe(this::finish, Throwable::printStackTrace));
    }

    private void finish() {
        dataStore.save(new FilterUpdatedUseCase());
        eventBus.send(ActivityEvent.build(this).finishActivity());
    }

    private void populateScreenForFilter(Filter filter) {
        selectedSex.set(filter.getSex());
        selectedAge.set(filter.getAge());
    }
}
