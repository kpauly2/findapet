package tech.pauly.findapet.discover;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.databinding.ViewAnimalListContainerBinding;
import tech.pauly.findapet.dependencyinjection.ForApplication;

public class AnimalTypeViewPagerAdapter extends PagerAdapter {

    private final Context context;
    private DiscoverViewModel viewModel;

    @Inject
    AnimalTypeViewPagerAdapter(@ForApplication Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewAnimalListContainerBinding binding = DataBindingUtil.inflate(inflater, R.layout.view_animal_list_container, container, false);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
        container.addView(binding.getRoot());
        return binding;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewAnimalListContainerBinding binding = (ViewAnimalListContainerBinding) object;
        binding.setViewModel(null);
        binding.executePendingBindings();
        container.removeView(binding.getRoot());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(AnimalType.values()[position].getFormattedName());
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((ViewDataBinding) object).getRoot() == view;
    }

    public void setViewModel(DiscoverViewModel viewModel) {
        this.viewModel = viewModel;
    }
}
