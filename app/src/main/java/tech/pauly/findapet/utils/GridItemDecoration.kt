package tech.pauly.findapet.utils

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View

private const val FULL_MARGIN_DP = 16f
private const val HALF_MARGIN_DP = 8f
private const val LARGE_MARGIN_DP = 24f

internal class GridItemDecoration(context: Context, orientation: Int) : DividerItemDecoration(context, orientation) {
    private val fullMargin: Int
    private val halfMargin: Int
    private val largeMargin: Int

    init {
        val metrics = context.resources.displayMetrics
        fullMargin = TypedValue.applyDimension(COMPLEX_UNIT_DIP, FULL_MARGIN_DP, metrics).toInt()
        halfMargin = TypedValue.applyDimension(COMPLEX_UNIT_DIP, HALF_MARGIN_DP, metrics).toInt()
        largeMargin = TypedValue.applyDimension(COMPLEX_UNIT_DIP, LARGE_MARGIN_DP, metrics).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        parent?.getChildAdapterPosition(view)?.let { position ->
            outRect.top = fullMargin
            outRect.bottom = halfMargin
            if (position % 2 == 0) {
                outRect.left = fullMargin
                outRect.right = halfMargin
            } else {
                outRect.right = fullMargin
                outRect.left = halfMargin
            }
        }
    }
}
