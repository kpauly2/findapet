package tech.pauly.findapet.discover;

import android.support.v7.widget.RecyclerView;

import tech.pauly.findapet.databinding.ItemAnimalListBinding;

public class AnimalListItemViewHolder extends RecyclerView.ViewHolder {

    private ItemAnimalListBinding binding;

    public AnimalListItemViewHolder(ItemAnimalListBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(AnimalListItemViewModel viewModel) {
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }
}
