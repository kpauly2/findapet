package tech.pauly.findapet.discover;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ActivityBreedBinding;
import tech.pauly.findapet.shared.BaseActivity;

public class BreedActivity extends BaseActivity {

    @Inject
    BreedViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityBreedBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_breed);
        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }
}
