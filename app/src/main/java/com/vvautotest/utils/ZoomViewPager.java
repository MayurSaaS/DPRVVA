package com.vvautotest.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class ZoomViewPager  extends ViewPager {

    public ZoomViewPager(Context context) {
        super(context);
    }

    public ZoomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView) {
            return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }

}