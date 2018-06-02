package tech.pauly.findapet.discover;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ActivityFilterBinding;
import tech.pauly.findapet.shared.BaseActivity;

public class FilterActivity extends BaseActivity {

    @Inject
    FilterViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityFilterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }
}
