package tech.pauly.findapet.utils;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import tech.pauly.findapet.R;

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

    @BindingAdapter("onCheckChanged")
    public static void setupRadioGroup(RadioGroup radioGroup, RadioGroupCheckChangedListener listener) {
        RadioGroup.OnCheckedChangeListener checkedChangeListener = (group, checkedId) -> {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                if (view.getId() == checkedId && !view.isChecked()) {
                    view.setChecked(true);
                } else {
                    view.setChecked(false);
                }
            }
            listener.checkChanged(checkedId);
        };
        for (int j = 0; j < radioGroup.getChildCount(); j++) {
            View view = radioGroup.getChildAt(j);
            view.setOnClickListener(v -> {
                radioGroup.clearCheck();
                radioGroup.check(view.getId());
            });
        }
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
        radioGroup.check(radioGroup.getChildAt(0).getId());
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
    public interface RadioGroupCheckChangedListener {
        void checkChanged(int position);
    }
}
