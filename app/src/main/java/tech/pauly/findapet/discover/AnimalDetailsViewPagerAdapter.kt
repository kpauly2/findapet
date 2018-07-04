package tech.pauly.findapet.discover

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.ViewAnimalDetailsDetailsBinding
import tech.pauly.findapet.databinding.ViewAnimalDetailsLocationBinding
import tech.pauly.findapet.dependencyinjection.ForApplication
import javax.inject.Inject

open class AnimalDetailsViewPagerAdapter @Inject
internal constructor(@ForApplication private val context: Context) : PagerAdapter() {

    var viewModel: AnimalDetailsViewModel? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        return when (position) {
            0 -> {
                ViewAnimalDetailsDetailsBinding.inflate(inflater, container, false).also {
                    it.viewModel = viewModel
                    it.executePendingBindings()
                    container.addView(it.root)
                }
            }
            1 -> {
                ViewAnimalDetailsLocationBinding.inflate(inflater, container, false).also {
                    it.viewModel = viewModel
                    it.executePendingBindings()
                    container.addView(it.root)
                }
            }
            else -> throw IllegalStateException(this::class.simpleName + " item at position " + position + " not supported")
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        when (position) {
            0 -> {
                (obj as? ViewAnimalDetailsDetailsBinding)?.run {
                    viewModel = null
                    executePendingBindings()
                    container.removeView(this.root)
                }
            }
            1 -> {
                (obj as? ViewAnimalDetailsLocationBinding)?.run {
                    viewModel = null
                    executePendingBindings()
                    container.removeView(this.root)
                }
            }
            else -> throw IllegalStateException(this::class.simpleName + " item at position " + position + " not supported")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.animal_details_tab_details)
            1 -> context.getString(R.string.animal_details_tab_location)
            else -> throw IllegalStateException(this::class.simpleName + " item at position " + position + " not supported")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return (obj as? ViewDataBinding)?.root === view
    }
}
