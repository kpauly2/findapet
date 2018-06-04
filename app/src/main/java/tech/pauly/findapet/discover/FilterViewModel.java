package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableBoolean;
import android.view.View;

import javax.inject.Inject;

import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

import static tech.pauly.findapet.data.models.Sex.U;

public class FilterViewModel extends BaseViewModel {

    public ObservableBoolean maleChecked = new ObservableBoolean(true);
    public ObservableBoolean femaleChecked = new ObservableBoolean(true);
    public ObservableBoolean babyChecked = new ObservableBoolean(true);
    public ObservableBoolean youngChecked = new ObservableBoolean(true);
    public ObservableBoolean adultChecked = new ObservableBoolean(true);
    public ObservableBoolean seniorChecked = new ObservableBoolean(true);

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

    public void saveFilter(View v) {
        Filter filter = new Filter();
        filter.setSex(getSex());
        subscribeOnLifecycle(filterRepository.insertFilter(filter)
                                             .subscribe(this::finish, Throwable::printStackTrace));
    }

    private void finish() {
        dataStore.save(new FilterUpdatedUseCase());
        eventBus.send(ActivityEvent.build(this).finishActivity());
    }

    private void populateScreenForFilter(Filter filter) {
        switch (filter.getSex()) {
            case U:
                maleChecked.set(true);
                femaleChecked.set(true);
                break;
            case M:
                maleChecked.set(true);
                femaleChecked.set(false);
                break;
            case F:
                maleChecked.set(false);
                femaleChecked.set(true);
                break;
        }
    }

    private Sex getSex() {
        if ((maleChecked.get() && femaleChecked.get())
            || (!maleChecked.get() && !femaleChecked.get())) {
            return U;
        } else if (maleChecked.get() && !femaleChecked.get()) {
            return Sex.M;
        } else {
            return Sex.F;
        }
    }
}
