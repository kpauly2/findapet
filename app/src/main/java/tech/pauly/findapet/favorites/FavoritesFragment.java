package tech.pauly.findapet.favorites;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.FragmentFavoritesBinding;

public class FavoritesFragment extends Fragment {

    @Inject
    FavoritesViewModel viewModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentFavoritesBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false);
        getLifecycle().addObserver(viewModel);
        return binding.getRoot();
    }
}
