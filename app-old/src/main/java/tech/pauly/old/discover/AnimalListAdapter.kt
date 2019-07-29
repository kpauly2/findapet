package tech.pauly.old.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import java.util.ArrayList

import javax.inject.Inject

import tech.pauly.old.R

open class AnimalListAdapter @Inject
internal constructor() : RecyclerView.Adapter<AnimalListItemViewHolder>() {

    open var animalItems = ArrayList<AnimalListItemViewModel>()
        set(value) {
            field.addAll(value)
            notifyDataSetChanged()
        }

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

    open fun clearAnimalItems() {
        animalItems.clear()
        notifyDataSetChanged()
    }

    open fun addAnimalItem(item: AnimalListItemViewModel) {
        animalItems.add(item)
        notifyDataSetChanged()
    }
}
