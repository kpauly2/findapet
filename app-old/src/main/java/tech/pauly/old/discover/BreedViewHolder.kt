package tech.pauly.old.discover

import androidx.recyclerview.widget.RecyclerView
import tech.pauly.old.databinding.ItemFilterBreedBinding

class BreedViewHolder(binding: ItemFilterBreedBinding) : RecyclerView.ViewHolder(binding.root) {

    private var binding: ItemFilterBreedBinding? = binding

    fun bind(viewModel: BreedViewModel, breed: String) {
        binding?.let {
            it.viewModel = viewModel
            it.breed = breed
            it.executePendingBindings()
        }
    }
}