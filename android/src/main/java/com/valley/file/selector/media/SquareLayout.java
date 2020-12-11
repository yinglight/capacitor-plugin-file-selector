package com.valley.file.selector.media;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareLayout extends RelativeLayout {
    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        widthMeasureSpec = heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
