package tech.pauly.old.discover

import androidx.recyclerview.widget.RecyclerView

import tech.pauly.old.databinding.ItemAnimalListBinding

class AnimalListItemViewHolder(private val binding: ItemAnimalListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: AnimalListItemViewModel) {
        binding.also {
            it.viewModel = viewModel
            it.executePendingBindings()
        }
    }
}
