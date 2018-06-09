package tech.pauly.findapet.discover;

import android.support.v7.widget.RecyclerView;

import tech.pauly.findapet.databinding.ItemFilterBreedBinding;
import tech.pauly.findapet.databinding.ItemFiltersBinding;

public class FilterBreedViewHolder extends RecyclerView.ViewHolder {

    private ItemFiltersBinding itemFiltersBinding;
    private ItemFilterBreedBinding itemFilterBreedBinding;

    public FilterBreedViewHolder(ItemFiltersBinding binding) {
        super(binding.getRoot());
        itemFiltersBinding = binding;
    }

    public FilterBreedViewHolder(ItemFilterBreedBinding binding) {
        super(binding.getRoot());
        itemFilterBreedBinding = binding;
    }

    public void bind(FilterViewModel viewModel) {
        itemFiltersBinding.setViewModel(viewModel);
    }

    public void bind(FilterViewModel viewModel, String breed) {
        itemFilterBreedBinding.setBreed(breed);
        itemFilterBreedBinding.setViewModel(viewModel);
    }
}