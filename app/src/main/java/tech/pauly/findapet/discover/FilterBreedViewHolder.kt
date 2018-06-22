package tech.pauly.findapet.discover

import android.support.v7.widget.RecyclerView
import tech.pauly.findapet.databinding.ItemBreedSearchBinding
import tech.pauly.findapet.databinding.ItemFilterBreedBinding
import tech.pauly.findapet.databinding.ItemFiltersBinding

class FilterBreedViewHolder : RecyclerView.ViewHolder {

    private var itemFiltersBinding: ItemFiltersBinding? = null
    private var itemFilterBreedBinding: ItemFilterBreedBinding? = null
    private var itemBreedSearchBinding: ItemBreedSearchBinding? = null

    constructor(binding: ItemFiltersBinding) : super(binding.root) {
        itemFiltersBinding = binding
    }

    constructor(binding: ItemFilterBreedBinding) : super(binding.root) {
        itemFilterBreedBinding = binding
    }

    constructor(binding: ItemBreedSearchBinding) : super(binding.root) {
        itemBreedSearchBinding = binding
    }

    fun bind(viewModel: FilterViewModel) {
        itemFiltersBinding?.let {
            it.viewModel = viewModel
            it.executePendingBindings()
            return
        }
        itemBreedSearchBinding?.let {
            it.viewModel = viewModel
            it.executePendingBindings()
        }
    }

    fun bind(viewModel: FilterViewModel, breed: String) {
        itemFilterBreedBinding?.let {
            it.breed = breed
            it.viewModel = viewModel
            it.executePendingBindings()
        }
    }
}