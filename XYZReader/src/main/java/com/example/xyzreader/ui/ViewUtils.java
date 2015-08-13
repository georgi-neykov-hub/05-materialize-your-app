package com.example.xyzreader.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.xyzreader.R;

public class ViewUtils {

    @SuppressLint("NewApi")
    public static void setElevation(View view, float elevationPixels){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(elevationPixels);
        }
    }

    @SuppressLint("NewApi")
    public static void setElevation(View view, @DimenRes int elevationDimen, Resources resources){
        setElevation(view, resources.getDimension(elevationDimen));
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(@DrawableRes int resId, Resources resources, Resources.Theme theme){
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(resId);
        }else{
            return resources.getDrawable(resId, theme);
        }
    }

    public static void hideSoftwareKeyboard(Activity activity){
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static @ColorRes int getThemeColorAccent(Resources.Theme theme){
        return getThemeAttribute(theme, R.attr.colorAccent);
    }

    public static int getThemeAttribute (Resources.Theme theme, int themeAttr) {
        final TypedValue value = new TypedValue();
        theme.resolveAttribute(themeAttr, value, true);
        return value.resourceId;
    }
}
