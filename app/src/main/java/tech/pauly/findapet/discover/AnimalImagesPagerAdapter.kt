package tech.pauly.findapet.discover

import android.databinding.ViewDataBinding
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.pauly.findapet.databinding.ItemAnimalImageBinding
import javax.inject.Inject

open class AnimalImagesPagerAdapter @Inject
internal constructor() : PagerAdapter() {

    private var imageViewModels: List<AnimalImageViewModel>? = null

    override fun getCount(): Int {
        return imageViewModels?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        return ItemAnimalImageBinding.inflate(inflater, container, false).apply {
            viewModel = imageViewModels?.get(position)
            executePendingBindings()
            container.addView(this.root)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (obj as? ItemAnimalImageBinding)?.apply {
            viewModel = null
            executePendingBindings()
            container.removeView(this.root)
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return (obj as? ViewDataBinding)?.root === view
    }

    open fun setAnimalImages(imageUrls: List<AnimalImageViewModel>) {
        this.imageViewModels = imageUrls
        notifyDataSetChanged()
    }
}
