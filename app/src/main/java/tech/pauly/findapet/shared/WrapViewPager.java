package tech.pauly.findapet.shared;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class WrapViewPager extends ViewPager {

    public WrapViewPager(Context context) {
        super(context);
    }

    public WrapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int currItem = getCurrentItem();
        if (currItem < 0) { currItem = 0; }
        View child = getChildAt(currItem);
        if (child != null) {
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height)
                height = h;

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

