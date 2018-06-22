package tech.pauly.findapet.discover

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import java.util.ArrayList

import javax.inject.Inject

import tech.pauly.findapet.databinding.ItemFilterBreedBinding
import tech.pauly.findapet.databinding.ItemBreedSearchBinding
import tech.pauly.findapet.databinding.ItemFiltersBinding

private const val FILTERS = 0
private const val BREED_SEARCH = 1
private const val BREED = 2

open class FilterAdapter @Inject
internal constructor() : RecyclerView.Adapter<FilterBreedViewHolder>() {

    private val breedItems = ArrayList<String>()
    open var viewModel: FilterViewModel? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterBreedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            FILTERS -> FilterBreedViewHolder(ItemFiltersBinding.inflate(inflater, parent, false))
            BREED_SEARCH -> FilterBreedViewHolder(ItemBreedSearchBinding.inflate(inflater, parent, false))
            BREED -> FilterBreedViewHolder(ItemFilterBreedBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException("Filter breed list view type not supported")
        }
    }

    override fun onBindViewHolder(holder: FilterBreedViewHolder, position: Int) {
        viewModel?.let {
            when (position) {
                0, 1 -> holder.bind(it)
                else -> holder.bind(it, breedItems[position - 2])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> FILTERS
            1 -> BREED_SEARCH
            else -> BREED
        }
    }

    override fun getItemCount(): Int {
        return breedItems.size + 2
    }

    open fun setBreedItems(newItems: List<String>?) {
        if (newItems == null) return
        val oldItems = ArrayList(breedItems)
        breedItems.clear()
        breedItems.addAll(newItems)
        if (newItems.size < oldItems.size) {
            val difference = oldItems.size - newItems.size
            notifyItemRangeRemoved(newItems.size + 2, difference + 2)
        }
        notifyItemRangeChanged(2, breedItems.size)
    }
}
