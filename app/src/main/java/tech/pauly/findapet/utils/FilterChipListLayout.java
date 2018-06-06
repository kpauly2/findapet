package tech.pauly.findapet.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.Chip;

public class FilterChipListLayout extends LinearLayout {

    private Context context;
    @Nullable
    private Chip locationChip;
    private List<Chip> filterChips = new ArrayList<>();

    public FilterChipListLayout(Context context) {
        super(context);
        setup(context);
    }

    public FilterChipListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public FilterChipListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
        this.context = context;
        updateLayout();
    }

    public void setLocationChip(Chip chip) {
        this.locationChip = chip;
        updateLayout();
    }

    public void setFilterChips(List<Chip> filterChips) {
        this.filterChips = filterChips;
        updateLayout();
    }

    private void updateLayout() {
        if (filterChips.size() > 0 || locationChip != null) {
            setVisibility(VISIBLE);
            removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (locationChip != null) {
                addChip(inflater, locationChip);
            }
            for (Chip chip : filterChips) {
                addChip(inflater, chip);
            }
        } else {
            setVisibility(GONE);
        }
        requestLayout();
    }

    private void addChip(LayoutInflater inflater, Chip chip) {
        FrameLayout chipLayout = (FrameLayout) inflater.inflate(R.layout.item_chip, this, false);
        ((TextView) chipLayout.getChildAt(0)).setText(chip.getText());
        addView(chipLayout);
    }
}