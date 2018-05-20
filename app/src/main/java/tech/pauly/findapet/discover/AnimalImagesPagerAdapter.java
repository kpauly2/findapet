package tech.pauly.findapet.discover;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ItemAnimalImageBinding;

public class AnimalImagesPagerAdapter extends PagerAdapter {

    private List<AnimalImageViewModel> imageViewModels;

    @Inject
    AnimalImagesPagerAdapter() {}

    @Override
    public int getCount() {
        return imageViewModels == null ? 0 : imageViewModels.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ItemAnimalImageBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_animal_image, container, false);
        binding.setViewModel(imageViewModels.get(position));
        binding.executePendingBindings();
        container.addView(binding.getRoot());
        return binding;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ItemAnimalImageBinding binding = (ItemAnimalImageBinding) object;
        binding.setViewModel(null);
        binding.executePendingBindings();
        container.removeView(binding.getRoot());
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((ViewDataBinding) object).getRoot() == view;
    }

    public void setAnimalImages(List<AnimalImageViewModel> imageUrls) {
        this.imageViewModels = imageUrls;
        notifyDataSetChanged();
    }
}
