package tech.pauly.findapet.discover;

import android.databinding.ObservableBoolean;

import javax.inject.Inject;

import tech.pauly.findapet.shared.BaseViewModel;

public class FilterViewModel extends BaseViewModel {

    public ObservableBoolean maleChecked = new ObservableBoolean(true);
    public ObservableBoolean femaleChecked = new ObservableBoolean(true);

    @Inject
    FilterViewModel() {}
}
