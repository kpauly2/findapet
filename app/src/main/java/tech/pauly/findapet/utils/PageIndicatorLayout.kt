package tech.pauly.findapet.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import tech.pauly.findapet.R

class PageIndicatorLayout : LinearLayout {

    private var indicatorCount = 0
    private var currentItem = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setupIndicators(amount: Int) {
        indicatorCount = amount
        updateLayout()
    }

    fun setCurrentItem(position: Int) {
        currentItem = position
        updateLayout()
    }

    private fun updateLayout() {
        if (indicatorCount == 1) {
            setMeasuredDimension(0, 0)
        } else {
            removeAllViews()
            val inflater = LayoutInflater.from(context)
            for (i in 0 until indicatorCount) {
                (inflater.inflate(R.layout.item_page_indicator, this, false) as FrameLayout).let {
                    it.getChildAt(0).isSelected = i == currentItem
                    addView(it)
                }
            }
        }
        requestLayout()
    }
}
