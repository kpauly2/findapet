package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableBoolean;
import android.view.View;

import javax.inject.Inject;

import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

public class FilterViewModel extends BaseViewModel {

    public ObservableBoolean maleChecked = new ObservableBoolean(true);
    public ObservableBoolean femaleChecked = new ObservableBoolean(true);
    private FilterRepository filterRepository;
    private ViewEventBus eventBus;

    @Inject
    FilterViewModel(FilterRepository filterRepository,
                    ViewEventBus eventBus) {
        this.filterRepository = filterRepository;
        this.eventBus = eventBus;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void loadCurrentFilter() {
        subscribeOnLifecycle(filterRepository.getCurrentFilter()
                                             .subscribe(this::populateScreenForFilter, Throwable::printStackTrace));
    }

    public void saveFilter(View v) {
        Filter filter = new Filter();
        filter.setSex(getSexInt());
        subscribeOnLifecycle(filterRepository.insertFilter(filter)
                                             .subscribe(this::finish, Throwable::printStackTrace));
    }

    private void finish() {
        eventBus.send(ActivityEvent.build(this).finishActivity());
    }

    private void populateScreenForFilter(Filter filter) {
        switch (filter.getSex()) {
            case 0:
                maleChecked.set(true);
                femaleChecked.set(true);
                break;
            case 1:
                maleChecked.set(true);
                femaleChecked.set(false);
                break;
            case 2:
                maleChecked.set(false);
                femaleChecked.set(true);
                break;
        }
    }

    private int getSexInt() {
        if ((maleChecked.get() && femaleChecked.get())
            || (!maleChecked.get() && !femaleChecked.get())) {
            return 0;
        } else if (maleChecked.get() && !femaleChecked.get()) {
            return 1;
        } else {
            return 2;
        }
    }
}
