package tech.pauly.findapet.utils

import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import tech.pauly.findapet.R

private const val DEFAULT_DURATION = 500
private const val COLLAPSED_VALUE = 0f
private const val EXPANDED_VALUE = 1f

class ExpandingLayout : LinearLayout {

    private enum class State {
        COLLAPSED,
        COLLAPSING,
        EXPANDED,
        EXPANDING
    }

    private val interpolator = FastOutSlowInInterpolator()
    private var animator: ValueAnimator? = null

    private var duration = 0L
    private var currentState = State.COLLAPSED
    private var currentAnimationValue = COLLAPSED_VALUE
    private var fullLayoutHeight = 0
    private var forceCollapse = false

    constructor(context: Context) : super(context) {
        setup(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context, attrs)
    }

    private fun setup(context: Context, attrs: AttributeSet?) {
        attrs?.apply {
            val a = context.obtainStyledAttributes(this, R.styleable.ExpandingLayout)
            try {
                duration = a.getInt(R.styleable.ExpandingLayout_duration, DEFAULT_DURATION).toLong()
            } finally {
                a.recycle()
            }
        }
        measureLayoutHeight()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val expansionSize = (fullLayoutHeight * currentAnimationValue).toInt()
        setMeasuredDimension(measuredWidth, expansionSize)
    }

    fun collapse() {
        if (currentState != State.COLLAPSED) {
            forceCollapse = true
            tapToggle()
        }
    }

    fun tapToggle() {
        animator?.let {
            it.cancel()
            animator = null
        }

        animator = if (forceCollapse || currentState == State.EXPANDING || currentState == State.EXPANDED) {
            ValueAnimator.ofFloat(currentAnimationValue, COLLAPSED_VALUE)
        } else {
            ValueAnimator.ofFloat(currentAnimationValue, EXPANDED_VALUE)
        }.also {
            forceCollapse = false
            it.setDuration(duration).interpolator = this.interpolator
            it.addUpdateListener { valueAnimator -> animate(valueAnimator.animatedValue as Float) }
            it.start()
        }
    }

    private fun animate(animationValue: Float) {
        val lastAnimationValue = currentAnimationValue
        currentAnimationValue = animationValue

        currentState = when {
            currentAnimationValue == COLLAPSED_VALUE -> State.COLLAPSED
            currentAnimationValue == EXPANDED_VALUE -> State.EXPANDED
            currentAnimationValue > lastAnimationValue -> State.EXPANDED
            else -> State.COLLAPSING
        }

        visibility = if (currentState == State.COLLAPSED) View.GONE else View.VISIBLE
        requestLayout()
    }

    private fun measureLayoutHeight() {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                for (i in 0 until childCount) {
                    fullLayoutHeight += getChildAt(i).height
                }
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                visibility = View.GONE
            }
        })
    }
}
