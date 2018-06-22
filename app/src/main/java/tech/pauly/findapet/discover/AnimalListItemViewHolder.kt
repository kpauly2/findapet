package tech.pauly.findapet.discover

import android.support.v7.widget.RecyclerView

import tech.pauly.findapet.databinding.ItemAnimalListBinding

class AnimalListItemViewHolder(private val binding: ItemAnimalListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: AnimalListItemViewModel) {
        binding.also {
            it.viewModel = viewModel
            it.executePendingBindings()
        }
    }
}
