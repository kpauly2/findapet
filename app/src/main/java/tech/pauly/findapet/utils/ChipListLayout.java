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

public class ChipListLayout extends LinearLayout {

    private Context context;
    private List<Chip> chips = new ArrayList<>();

    public ChipListLayout(Context context) {
        super(context);
        setup(context);
    }

    public ChipListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public ChipListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
        this.context = context;
        updateLayout();
    }

    public void setChips(List<Chip> chips) {
        this.chips = chips;
        updateLayout();
    }

    private void updateLayout() {
        if (chips.size() > 0) {
            setVisibility(VISIBLE);
            removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (Chip chip : chips) {
                FrameLayout chipLayout = (FrameLayout) inflater.inflate(R.layout.item_chip, this, false);
                ((TextView) chipLayout.getChildAt(0)).setText(chip.getText());
                addView(chipLayout);
            }
        } else {
            setVisibility(GONE);
        }
        requestLayout();
    }
}