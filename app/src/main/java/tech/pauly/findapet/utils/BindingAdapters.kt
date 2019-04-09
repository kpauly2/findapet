package tech.pauly.findapet.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.fullUrl

object BindingAdapters {
    var recyclerItemWidth: Int = 0
}

enum class DrawablePosition {
    START, TOP, END, BOTTOM
}

@BindingAdapter(value = ["navigationItemSelectedListener", "defaultSelectedItem"])
fun setupBottomNavigationView(bottomNavigationView: BottomNavigationView,
                              listener: BottomNavigationView.OnNavigationItemSelectedListener,
                              @IdRes selectedItemResId: Int) {
    bottomNavigationView.setOnNavigationItemSelectedListener(listener)
    bottomNavigationView.selectedItemId = selectedItemResId
}

@BindingAdapter(value = ["imageUrl", "cornerRadius"])
fun loadImageIntoView(view: ImageView, url: String?, cornerRadius: Int) {
    url ?: return
    val transformation = RoundedCornersTransformation(view.context.dp2px(cornerRadius.toFloat()), 0)
    Picasso.get()
            .load(url.fullUrl)
            .error(R.drawable.shape_animal_image)
            .fit()
            .centerCrop()
            .transform(transformation)
            .into(view)
}

@BindingAdapter("layoutManager")
fun layoutManager(recyclerView: RecyclerView, orientation: Int) {
    recyclerView.layoutManager = when (orientation) {
        RecyclerView.HORIZONTAL -> LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
        else -> LinearLayoutManager(recyclerView.context)
    }
}

@BindingAdapter(value = ["recyclerViewGridSpan", "loadMoreListener"])
fun setupRecyclerView(recyclerView: RecyclerView, span: Int, listener: RecyclerViewLoadMoreDataListener) {
    setupBaseRecyclerViewLayout(recyclerView, span)

    val scrollListener = object : EndlessRecyclerViewScrollListener(recyclerView.layoutManager as GridLayoutManager) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            listener.loadMore()
        }
    }
    recyclerView.addOnScrollListener(scrollListener)
}

@BindingAdapter("recyclerViewGridSpan")
fun setupRecyclerView(recyclerView: RecyclerView, span: Int) {
    setupBaseRecyclerViewLayout(recyclerView, span)
}

@BindingAdapter("height")
fun setCustomLayoutHeight(view: View, height: Int) {
    view.layoutParams = view.layoutParams.also {
        it.height = height
    }
}

@BindingAdapter("width")
fun setCustomLayoutWidth(view: View, width: Int) {
    view.layoutParams = view.layoutParams.also {
        it.width = width
    }
}

@BindingAdapter("viewPager")
fun setViewPager(tabLayout: TabLayout, viewPager: ViewPager) {
    tabLayout.setupWithViewPager(viewPager)
}

@BindingAdapter("onPageChange")
fun onPageChange(viewPager: ViewPager, listener: ViewPagerPageChangeListener) {
    val pageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            listener.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
    viewPager.addOnPageChangeListener(pageChangeListener)
    viewPager.post { pageChangeListener.onPageSelected(0) }
}

@BindingAdapter("offscreenPageLimit")
fun offscreenPageLimit(viewPager: ViewPager, limit: Int) {
    viewPager.offscreenPageLimit = limit
}

@BindingAdapter("indicatorCount")
fun indicatorCount(layout: PageIndicatorLayout, count: Int) {
    layout.setupIndicators(count)
}

@BindingAdapter("indicatorPosition")
fun indicatorPosition(layout: PageIndicatorLayout, position: Int) {
    layout.setCurrentItem(position)
}

@BindingAdapter("swipeRefreshListener")
fun setupSwipeRefreshLayout(layout: SwipeRefreshLayout, listener: SwipeRefreshListener) {
    layout.setOnRefreshListener(listener::onRefresh)
    layout.setColorSchemeColors(layout.context.getColor(R.color.purpleStandard))
}

@BindingAdapter("refreshing")
fun refreshing(layout: SwipeRefreshLayout, refreshing: Boolean) {
    layout.isRefreshing = refreshing
}

@BindingAdapter("chipList")
fun chipList(layout: FilterChipListLayout, chips: List<Chip>) {
    layout.setFilterChips(chips)
}

@BindingAdapter("locationChip")
fun locationChip(layout: FilterChipListLayout, chip: Chip?) {
    layout.setLocationChip(chip)
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("touchListener")
fun setFocusChangeListener(view: View, touchListener: TouchListener) {
    view.setOnTouchListener { _, event ->
        if (MotionEvent.ACTION_UP == event.action) {
            touchListener.onTouch()
        }
        false
    }
}

@BindingAdapter(value = ["dynamicDrawable", "dynamicDrawableVisibility", "drawablePosition"])
fun toggleDynamicTextViewDrawable(view: TextView, drawable: Drawable, visible: Boolean, position: DrawablePosition) {
    if (visible) {
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (position == DrawablePosition.START) drawable else null,
                if (position == DrawablePosition.TOP) drawable else null,
                if (position == DrawablePosition.END) drawable else null,
                if (position == DrawablePosition.BOTTOM) drawable else null)
        view.invalidate()
    } else {
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }
}

@BindingAdapter("backgroundColorFilter")
fun setBackgroundColorFilter(view: View, color: Int) {
    view.background.setColorFilter(view.context.getColor(color), PorterDuff.Mode.SRC_ATOP)
}

@FunctionalInterface
interface ViewPagerPageChangeListener {
    fun onPageSelected(position: Int)
}

@FunctionalInterface
interface RecyclerViewLoadMoreDataListener {
    fun loadMore()
}

@FunctionalInterface
interface SwipeRefreshListener {
    fun onRefresh()
}

@FunctionalInterface
interface TouchListener {
    fun onTouch()
}

fun Context.dp2px(dp: Float): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

private fun setupBaseRecyclerViewLayout(recyclerView: RecyclerView, span: Int) {
    recyclerView.layoutManager = GridLayoutManager(recyclerView.context, span)
    recyclerView.addItemDecoration(GridItemDecoration(recyclerView.context, GridLayoutManager.HORIZONTAL))

    val widthInDp = recyclerView.resources.displayMetrics.widthPixels.toFloat()
    val edgeMargin = 16
    val centerMargin = 8
    val marginSize = (edgeMargin * span + centerMargin) * recyclerView.resources.displayMetrics.density
    BindingAdapters.recyclerItemWidth = (widthInDp - marginSize).toInt() / span
}