package tech.pauly.findapet.utils;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import tech.pauly.findapet.R;

public class BindingAdapters {

    public static int recyclerItemWidth;

    @BindingAdapter("navigationItemSelectedListener")
    public static void navigationItemSelectedListener(BottomNavigationView bottomNavigationView, BottomNavigationView.OnNavigationItemSelectedListener listener) {
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    @BindingAdapter("defaultSelectedItem")
    public static void defaultSelectedItem(BottomNavigationView bottomNavigationView, @IdRes int selectedItemResId) {
        bottomNavigationView.setSelectedItemId(selectedItemResId);
    }

    @BindingAdapter("imageUrl")
    public static void loadImageIntoView(ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            final Transformation transformation = new RoundedCornersTransformation(10, 0);
            Picasso.with(view.getContext())
                   .load(Uri.parse(url))
                   .fit()
                   .centerCrop()
                   .transform(transformation)
                   .into(view);
        }
    }

    @BindingAdapter("gridLayoutManager")
    public static void setGridLayoutManager(RecyclerView recyclerView, int span) {
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), span);
        GridItemDecoration dividerItemDecoration = new GridItemDecoration(recyclerView.getContext(), GridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @BindingAdapter("setGridItemWidth")
    public static void setGridItemWidth(RecyclerView recyclerView, int span) {
        float widthInDp = recyclerView.getResources().getDisplayMetrics().widthPixels;
        int edgeMargin = 16;
        int centerMargin = 8;
        float marginSize = ((edgeMargin * span) + centerMargin) * recyclerView.getResources().getDisplayMetrics().density;
        BindingAdapters.recyclerItemWidth = (int) (widthInDp - marginSize) / span;
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
}
