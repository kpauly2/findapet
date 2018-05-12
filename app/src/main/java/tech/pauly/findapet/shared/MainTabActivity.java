package tech.pauly.findapet.shared;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ActivityMainTabBinding;

public class MainTabActivity extends BaseActivity {

    @Inject
    MainTabViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityMainTabBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_tab);
        viewModel.setActivity(this);
        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);
    }

    public void launchTabFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
    }
}
