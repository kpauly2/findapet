package tech.pauly.findapet.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tech.pauly.findapet.R;

import static android.view.Gravity.CENTER;

public class PageIndicatorLayout extends LinearLayout {
    private int indicatorCount;
    private int currentItem = 0;
    private int margin = 8;
    private Context context;

    public PageIndicatorLayout(Context context) {
        super(context);
        setup(context);
    }

    public PageIndicatorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public PageIndicatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
        this.context = context;
    }

    public void setupIndicators(int amount) {
        indicatorCount = amount;
        updateLayout();
    }

    public void setCurrentItem(int position) {
        currentItem = position;
        updateLayout();
    }

    private void updateLayout() {
        if (indicatorCount == 1) {
            setMeasuredDimension(0, 0);
        } else {
            removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (int i = 0; i < indicatorCount; i++) {
                FrameLayout indicator = (FrameLayout) inflater.inflate(R.layout.item_page_indicator, this, false);
                indicator.getChildAt(0).setSelected(i == currentItem);
                addView(indicator);
            }
        }
        requestLayout();
    }
}
