package tech.pauly.findapet;

import android.databinding.BindingAdapter;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomNavigationView;
import android.view.View;

public class BindingAdapters {
    @BindingAdapter("navigationItemSelectedListener")
    public static void navigationItemSelectedListener(BottomNavigationView bottomNavigationView, BottomNavigationView.OnNavigationItemSelectedListener listener) {
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    @BindingAdapter("defaultSelectedItem")
    public static void defaultSelectedItem(BottomNavigationView bottomNavigationView, @IdRes int selectedItemResId) {
        bottomNavigationView.setSelectedItemId(selectedItemResId);
    }
}
