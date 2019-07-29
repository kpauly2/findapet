package tech.pauly.old.shared

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

class WrapViewPager : ViewPager {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newHeight = heightMeasureSpec
        val currItem = if (currentItem >= 0) currentItem else 0
        getChildAt(currItem)?.also {
            it.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            newHeight = View.MeasureSpec.makeMeasureSpec(it.measuredHeight, View.MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, newHeight)
    }
}

