package tech.pauly.findapet.discover

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import tech.pauly.findapet.databinding.ItemFilterBreedBinding
import javax.inject.Inject

class BreedAdapter @Inject
internal constructor() : RecyclerView.Adapter<BreedViewHolder>() {

    var viewModel: BreedViewModel? = null
    private var breedItems: List<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BreedViewHolder(ItemFilterBreedBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: BreedViewHolder, position: Int) {
        viewModel?.let {
            holder.bind(it, breedItems[position])
        }
    }

    override fun getItemCount(): Int {
        return breedItems.size
    }

    fun addBreeds(newItems: List<String>) {
        val oldItems = ArrayList(breedItems)
        breedItems = newItems
        if (newItems.size < oldItems.size) {
            val difference = oldItems.size - newItems.size
            notifyItemRangeRemoved(newItems.size, difference)
        }
        notifyItemRangeChanged(0, breedItems.size)
    }
}
