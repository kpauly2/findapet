package tech.pauly.findapet.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import tech.pauly.findapet.R
import java.util.*

data class Chip(val text: String)

class FilterChipListLayout : LinearLayout {

    private var locationChip: Chip? = null
    private var filterChips: List<Chip> = ArrayList()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        updateLayout()
    }

    fun setLocationChip(chip: Chip?) {
        this.locationChip = chip
        updateLayout()
    }

    fun setFilterChips(filterChips: List<Chip>) {
        this.filterChips = filterChips
        updateLayout()
    }

    private fun updateLayout() {
        removeAllViews()
        val inflater = LayoutInflater.from(context)
        locationChip?.also { addChip(inflater, it) }
        filterChips.forEach { addChip(inflater, it) }
        visibility = if (childCount == 0) View.GONE else View.VISIBLE
        requestLayout()
    }

    private fun addChip(inflater: LayoutInflater, chip: Chip) {
        (inflater.inflate(R.layout.item_chip, this, false) as FrameLayout).let {
            (it.getChildAt(0) as? TextView)?.text = chip.text
            addView(it)
        }
    }
}