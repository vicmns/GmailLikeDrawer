package com.vicmns.demogmaillikedrawer.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by vicmns on 10/28/2014.
 */
public class DrawerHeaderImageView extends ImageView {
    public DrawerHeaderImageView(Context context) {
        super(context);
    }

    public DrawerHeaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerHeaderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawerHeaderImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = (int) (width / (16.0/9.0));

        setMeasuredDimension(width, height);
    }
}
