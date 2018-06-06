package tech.pauly.findapet.utils;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.Chip;

public class BindingAdapters {

    public static int recyclerItemWidth;

    @BindingAdapter(value = {"navigationItemSelectedListener", "defaultSelectedItem"})
    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView, BottomNavigationView.OnNavigationItemSelectedListener listener, @IdRes int selectedItemResId) {
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        bottomNavigationView.setSelectedItemId(selectedItemResId);
    }

    @BindingAdapter("imageUrl")
    public static void loadImageIntoView(ImageView view, String url) {
        final Transformation transformation = new RoundedCornersTransformation(10, 0);
        Picasso.with(view.getContext())
               .load(Uri.parse(url))
               .error(R.drawable.shape_animal_image)
               .fit()
               .centerCrop()
               .transform(transformation)
               .into(view);
    }

    @BindingAdapter(value = {"recyclerViewGridSpan", "loadMoreListener"})
    public static void setupRecyclerView(RecyclerView recyclerView, int span, RecyclerViewLoadMoreDataListener listener) {
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), span);
        GridItemDecoration dividerItemDecoration = new GridItemDecoration(recyclerView.getContext(), GridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        float widthInDp = recyclerView.getResources().getDisplayMetrics().widthPixels;
        int edgeMargin = 16;
        int centerMargin = 8;
        float marginSize = ((edgeMargin * span) + centerMargin) * recyclerView.getResources().getDisplayMetrics().density;
        BindingAdapters.recyclerItemWidth = (int) (widthInDp - marginSize) / span;

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                listener.loadMore();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    @BindingAdapter("height")
    public static void setCustomLayoutHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("width")
    public static void setCustomLayoutWidth(View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("viewPager")
    public static void setViewPager(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
    }

    @BindingAdapter("onPageChange")
    public static void onPageChange(ViewPager viewPager, ViewPagerPageChangeListener listener) {
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                listener.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        };
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.post(() -> pageChangeListener.onPageSelected(0));
    }

    @BindingAdapter("offscreenPageLimit")
    public static void offscreenPageLimit(ViewPager viewPager, int limit) {
        viewPager.setOffscreenPageLimit(limit);
    }

    @BindingAdapter("indicatorCount")
    public static void indicatorCount(PageIndicatorLayout layout, int count) {
        layout.setupIndicators(count);
    }

    @BindingAdapter("indicatorPosition")
    public static void indicatorPosition(PageIndicatorLayout layout, int position) {
        layout.setCurrentItem(position);
    }

    @BindingAdapter("swipeRefreshListener")
    public static void setupSwipeRefreshLayout(SwipeRefreshLayout layout, SwipeRefreshListener listener) {
        layout.setOnRefreshListener(listener::onRefresh);
        layout.setColorSchemeColors(layout.getContext().getColor(R.color.purpleStandard));
    }

    @BindingAdapter("refreshing")
    public static void refreshing(SwipeRefreshLayout layout, boolean refreshing) {
        layout.setRefreshing(refreshing);
    }

    @BindingAdapter("chipList")
    public static void chipList(FilterChipListLayout layout, List<Chip> chips) {
        layout.setFilterChips(chips);
    }

    @BindingAdapter("locationChip")
    public static void locationChip(FilterChipListLayout layout, Chip chip) {
        layout.setLocationChip(chip);
    }

    @FunctionalInterface
    public interface ViewPagerPageChangeListener {
        void onPageSelected(int position);
    }

    @FunctionalInterface
    public interface RecyclerViewLoadMoreDataListener {
        void loadMore();
    }

    @FunctionalInterface
    public interface SwipeRefreshListener {
        void onRefresh();
    }
}
