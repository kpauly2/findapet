package tech.pauly.findapet.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

class GridItemDecoration extends DividerItemDecoration {

    private static final int FULL_MARGIN_DP = 16;
    private static final int HALF_MARGIN_DP = 8;
    private static final int LARGE_MARGIN_DP = 24;
    private int fullMargin;
    private int halfMargin;
    private int largeMargin;

    public GridItemDecoration(Context context, int orientation) {
        super(context, orientation);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        fullMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, FULL_MARGIN_DP, metrics);
        halfMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HALF_MARGIN_DP, metrics);
        largeMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LARGE_MARGIN_DP, metrics);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        outRect.top = largeMargin;
        if (itemPosition % 2 == 0) {
            outRect.left = fullMargin;
            outRect.right = halfMargin;
        } else {
            outRect.right = fullMargin;
            outRect.left = halfMargin;
        }
    }
}
