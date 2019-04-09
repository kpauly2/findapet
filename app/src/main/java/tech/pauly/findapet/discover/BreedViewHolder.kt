package tech.pauly.findapet.discover

import androidx.recyclerview.widget.RecyclerView
import tech.pauly.findapet.databinding.ItemFilterBreedBinding

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