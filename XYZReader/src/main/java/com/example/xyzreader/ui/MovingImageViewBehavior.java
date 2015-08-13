package com.example.xyzreader.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.xyzreader.R;

/**
 * Created by Georgi on 10.8.2015 г..
 */
public class MovingImageViewBehavior extends CoordinatorLayout.Behavior<View> {

    private static final float TRANSLATION_SCALE = 1f;

    private View mScrollChild;

    public MovingImageViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        mScrollChild = ((NestedScrollView) directTargetChild).getChildAt(0);
        return true;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if(!topReached(mScrollChild, target) && !bottomReached(mScrollChild, target)){
            child.setTranslationY(mScrollChild.getTop() * TRANSLATION_SCALE);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        mScrollChild = null;
    }

    private boolean topReached(View childView, View target){
        return target.getScrollY() <=0;
    }

    private boolean bottomReached(View childView, View target) {
        int diff = (childView.getBottom() - (target.getHeight() + target.getScrollY()));
        return diff <= 0;
    }

    private void translateChildViewBy(View child, int amount){
        float newTranslationY = child.getTranslationY() - amount * TRANSLATION_SCALE;
        child.setTranslationY(newTranslationY);
    }
}
