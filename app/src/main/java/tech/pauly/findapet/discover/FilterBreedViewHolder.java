package tech.pauly.findapet.discover;

import android.support.v7.widget.RecyclerView;

import tech.pauly.findapet.databinding.ItemFilterBreedBinding;
import tech.pauly.findapet.databinding.ItemFiltersBinding;
import tech.pauly.findapet.databinding.ItemBreedSearchBinding;

public class FilterBreedViewHolder extends RecyclerView.ViewHolder {

    private ItemFiltersBinding itemFiltersBinding;
    private ItemFilterBreedBinding itemFilterBreedBinding;
    private ItemBreedSearchBinding itemBreedSearchBinding;

    public FilterBreedViewHolder(ItemFiltersBinding binding) {
        super(binding.getRoot());
        itemFiltersBinding = binding;
    }

    public FilterBreedViewHolder(ItemFilterBreedBinding binding) {
        super(binding.getRoot());
        itemFilterBreedBinding = binding;
    }

    public FilterBreedViewHolder(ItemBreedSearchBinding binding) {
        super(binding.getRoot());
        itemBreedSearchBinding = binding;
    }

    public void bind(FilterViewModel viewModel) {
        if (itemFiltersBinding != null) {
            itemFiltersBinding.setViewModel(viewModel);
            itemFiltersBinding.executePendingBindings();
        } else if (itemBreedSearchBinding != null) {
            itemBreedSearchBinding.setViewModel(viewModel);
            itemBreedSearchBinding.executePendingBindings();
        }
    }

    public void bind(FilterViewModel viewModel, String breed) {
        itemFilterBreedBinding.setBreed(breed);
        itemFilterBreedBinding.setViewModel(viewModel);
        itemFilterBreedBinding.executePendingBindings();
    }
}