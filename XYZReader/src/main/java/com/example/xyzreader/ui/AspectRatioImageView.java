package com.example.xyzreader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.example.xyzreader.R;

public class AspectRatioImageView extends NetworkImageView {
    public static final float UNDEFINED_ASPECT_RATIO = 0.f;

    private float mAspectRatio = UNDEFINED_ASPECT_RATIO;

    public AspectRatioImageView(Context context) {
        this(context, null);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(attrs != null){
            readAttributeValues(context, attrs, defStyle);
        }

    }

    private void readAttributeValues(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attrsArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView, defStyleAttr, 0);
        try {
            mAspectRatio = attrsArray.getFloat(R.styleable.AspectRatioImageView_widthToHeightRatio, UNDEFINED_ASPECT_RATIO);
        } finally {
            attrsArray.recycle();
        }
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mAspectRatio != UNDEFINED_ASPECT_RATIO) {
            int measuredWidth = getMeasuredWidth();
            setMeasuredDimension(measuredWidth, (int) (measuredWidth / mAspectRatio));

        }
    }
}
