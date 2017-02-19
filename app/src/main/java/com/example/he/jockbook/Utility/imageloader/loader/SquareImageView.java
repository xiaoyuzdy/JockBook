package com.example.he.jockbook.Utility.imageloader.loader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 正方形的ImageView
 * Created by he on 2017/1/9.
 */

public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
