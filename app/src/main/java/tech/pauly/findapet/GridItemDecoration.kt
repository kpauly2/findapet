package tech.pauly.findapet

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

private const val FULL_MARGIN_DP = 16f
private const val HALF_MARGIN_DP = 8f

internal class GridItemDecoration(context: Context, orientation: Int) :
    DividerItemDecoration(context, orientation) {

    private val metrics = context.resources.displayMetrics
    private val fullMargin = TypedValue.applyDimension(COMPLEX_UNIT_DIP, FULL_MARGIN_DP, metrics).toInt()
    private val halfMargin = TypedValue.applyDimension(COMPLEX_UNIT_DIP, HALF_MARGIN_DP, metrics).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.getChildAdapterPosition(view).let { position ->
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
