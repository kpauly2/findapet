package tech.pauly.findapet.utils

import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.net.Uri
import android.support.annotation.IdRes
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import tech.pauly.findapet.R
import tech.pauly.findapet.discover.Chip

object BindingAdapters {
    var recyclerItemWidth: Int = 0
        get() = field
}

@BindingAdapter(value = ["navigationItemSelectedListener", "defaultSelectedItem"])
fun setupBottomNavigationView(bottomNavigationView: BottomNavigationView,
                              listener: BottomNavigationView.OnNavigationItemSelectedListener,
                              @IdRes selectedItemResId: Int) {
    bottomNavigationView.setOnNavigationItemSelectedListener(listener)
    bottomNavigationView.selectedItemId = selectedItemResId
}

@BindingAdapter("imageUrl")
fun loadImageIntoView(view: ImageView, url: String?) {
    if (url == null) {
        return
    }
    val transformation = RoundedCornersTransformation(10, 0)
    Picasso.with(view.context)
            .load(Uri.parse(url))
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
    recyclerView.layoutManager = GridLayoutManager(recyclerView.context, span)
    recyclerView.addItemDecoration(GridItemDecoration(recyclerView.context, GridLayoutManager.HORIZONTAL))

    val widthInDp = recyclerView.resources.displayMetrics.widthPixels.toFloat()
    val edgeMargin = 16
    val centerMargin = 8
    val marginSize = (edgeMargin * span + centerMargin) * recyclerView.resources.displayMetrics.density
    BindingAdapters.recyclerItemWidth = (widthInDp - marginSize).toInt() / span

    val scrollListener = object : EndlessRecyclerViewScrollListener(recyclerView.layoutManager as GridLayoutManager) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            listener.loadMore()
        }
    }
    recyclerView.addOnScrollListener(scrollListener)
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
    view.setOnTouchListener { v, event ->
        if (MotionEvent.ACTION_UP == event.action) {
            touchListener.onTouch()
        }
        false
    }
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