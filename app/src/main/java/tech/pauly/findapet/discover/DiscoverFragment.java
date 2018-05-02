package tech.pauly.findapet.discover;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.FragmentDiscoverBinding;

public class DiscoverFragment extends Fragment {

    @Inject
    DiscoverViewModel discoverViewModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDiscoverBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false);
        binding.setViewModel(discoverViewModel);
        return binding.getRoot();
    }
}
