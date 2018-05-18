package tech.pauly.findapet.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import tech.pauly.findapet.R;

public class ExpandingLayout extends LinearLayout {

    private static final int DEFAULT_DURATION = 500;

    private enum State {
        COLLAPSED,
        COLLAPSING,
        EXPANDED,
        EXPANDING
    }

    private final FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();
    private ValueAnimator animator;

    private int duration;
    private State currentState = State.COLLAPSED;
    private float currentAnimationValue = 0;
    private int fullLayoutHeight = 0;
    private boolean forceCollapse = false;

    public ExpandingLayout(@NonNull Context context) {
        super(context);
        setup(context, null);
    }

    public ExpandingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public ExpandingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int expansionSize = (int) (fullLayoutHeight * currentAnimationValue);
        setMeasuredDimension(getMeasuredWidth(), expansionSize);
    }

    public void collapse() {
        if (currentState != State.COLLAPSED) {
            forceCollapse = true;
            tapToggle();
        }
    }

    public void tapToggle() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        if (forceCollapse || currentState == State.EXPANDING || currentState == State.EXPANDED) {
                animator = ValueAnimator.ofFloat(currentAnimationValue, 0);
        } else {
            animator = ValueAnimator.ofFloat(currentAnimationValue, 1);
        }
        forceCollapse = false;

        animator.setDuration(duration)
                .setInterpolator(interpolator);
        animator.addUpdateListener(valueAnimator -> animate((float) valueAnimator.getAnimatedValue()));
        animator.start();
    }

    private void animate(float animationValue) {
        float lastAnimationValue = currentAnimationValue;
        currentAnimationValue = animationValue;

        if (currentAnimationValue == 0) {
            currentState = State.COLLAPSED;
        } else if (currentAnimationValue == 1) {
            currentState = State.EXPANDED;
        } else {
            if (currentAnimationValue > lastAnimationValue) {
                currentState = State.EXPANDING;
            } else {
                currentState = State.COLLAPSING;
            }
        }

        setVisibility(currentState == State.COLLAPSED ? GONE : VISIBLE);
        requestLayout();
    }

    private void setup(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandingLayout);
            try {
                duration = a.getInt(R.styleable.ExpandingLayout_duration, DEFAULT_DURATION);
            } finally {
                a.recycle();
            }
        }
        measureLayoutHeight();
    }

    private void measureLayoutHeight() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (int i = 0; i < getChildCount(); i++) {
                    fullLayoutHeight += getChildAt(i).getHeight();
                }
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setVisibility(GONE);
            }
        });
    }
}
