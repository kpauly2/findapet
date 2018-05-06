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
import tech.pauly.findapet.databinding.ViewAnimalDetailsDetailsBinding;
import tech.pauly.findapet.databinding.ViewAnimalDetailsLocationBinding;
import tech.pauly.findapet.dependencyinjection.ForApplication;

public class AnimalDetailsViewPagerAdapter extends PagerAdapter {

    private final Context context;
    private AnimalDetailsViewModel viewModel;

    @Inject
    AnimalDetailsViewPagerAdapter(@ForApplication Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        switch (position) {
            case 0:
                ViewAnimalDetailsDetailsBinding detailsBinding = DataBindingUtil.inflate(inflater, R.layout.view_animal_details_details, container, false);
                detailsBinding.setViewModel(viewModel);
                detailsBinding.executePendingBindings();
                container.addView(detailsBinding.getRoot());
                return detailsBinding;
            case 1:
                ViewAnimalDetailsLocationBinding locationBinding = DataBindingUtil.inflate(inflater, R.layout.view_animal_details_location, container, false);
                locationBinding.setViewModel(viewModel);
                locationBinding.executePendingBindings();
                container.addView(locationBinding.getRoot());
                return locationBinding;
            default:
                throw new IllegalStateException(getClass().getCanonicalName() + " item at position " + position + " not supported");
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        switch (position) {
            case 0:
                ViewAnimalDetailsDetailsBinding detailsBinding = (ViewAnimalDetailsDetailsBinding) object;
                detailsBinding.setViewModel(null);
                detailsBinding.executePendingBindings();
                container.removeView(detailsBinding.getRoot());
                return;
            case 1:
                ViewAnimalDetailsLocationBinding locationBinding = (ViewAnimalDetailsLocationBinding) object;
                locationBinding.setViewModel(null);
                locationBinding.executePendingBindings();
                container.removeView(locationBinding.getRoot());
            default:
                throw new IllegalStateException(getClass().getCanonicalName() + " item at position " + position + " not supported");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.animal_details_tab_details);
            case 1:
                return context.getString(R.string.animal_details_tab_location);
            default:
                throw new IllegalStateException(getClass().getCanonicalName() + " item at position " + position + " not supported");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((ViewDataBinding) object).getRoot() == view;
    }

    public void setViewModel(AnimalDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }
}
