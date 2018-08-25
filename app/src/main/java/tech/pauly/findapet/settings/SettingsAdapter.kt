package tech.pauly.findapet.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import tech.pauly.findapet.databinding.ItemSettingsBasicBinding
import tech.pauly.findapet.databinding.ItemSettingsTitleBinding
import javax.inject.Inject

const val TITLE = 0
const val LINK_OUT = 1
const val EMAIL = 2
const val CUSTOM = 3

open class SettingsAdapter @Inject
internal constructor() : RecyclerView.Adapter<SettingsViewHolder>() {

    open var viewModels = ArrayList<SettingsItemViewModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TITLE -> SettingsViewHolder(ItemSettingsTitleBinding.inflate(inflater, parent, false))
            LINK_OUT, EMAIL, CUSTOM -> SettingsViewHolder(ItemSettingsBasicBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException("Settings adapter view type not supported")
        }
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        viewModels.getOrNull(position)?.let {
            when (it.viewType) {
                TITLE -> holder.bind(it as SettingsTitleViewModel)
                LINK_OUT -> holder.bind(it as SettingsBasicViewModel)
                CUSTOM -> holder.bind(it as SettingsCustomViewModel)
                else -> throw IllegalStateException("Settings adapter view type not supported")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewModels.getOrNull(position)?.viewType ?: 0
    }

    override fun getItemCount(): Int {
        return viewModels.size
    }
}

class SettingsViewHolder : RecyclerView.ViewHolder {

    private var itemSettingsTitleBinding: ItemSettingsTitleBinding? = null
    private var itemSettingsBasicBinding: ItemSettingsBasicBinding? = null

    constructor(binding: ItemSettingsTitleBinding) : super(binding.root) {
        itemSettingsTitleBinding = binding
    }

    constructor(binding: ItemSettingsBasicBinding) : super(binding.root) {
        itemSettingsBasicBinding = binding
    }

    fun bind(viewModel: SettingsTitleViewModel) {
        itemSettingsTitleBinding?.viewModel = viewModel
    }

    fun bind(viewModel: SettingsBasicViewModel) {
        itemSettingsBasicBinding?.viewModel = viewModel
    }

}
