package tech.pauly.findapet.discover

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import java.util.ArrayList

import javax.inject.Inject

import tech.pauly.findapet.R

class AnimalListAdapter @Inject
internal constructor() : RecyclerView.Adapter<AnimalListItemViewHolder>() {

    private val animalItems = ArrayList<AnimalListItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalListItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AnimalListItemViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_animal_list, parent, false))
    }

    override fun onBindViewHolder(holder: AnimalListItemViewHolder, position: Int) {
        holder.bind(animalItems[position])
    }

    override fun getItemCount(): Int {
        return animalItems.size
    }

    fun setAnimalItems(items: List<AnimalListItemViewModel>) {
        animalItems.addAll(items)
        notifyDataSetChanged()
    }

    fun clearAnimalItems() {
        animalItems.clear()
        notifyDataSetChanged()
    }
}
