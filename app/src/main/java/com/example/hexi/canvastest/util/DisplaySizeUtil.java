package com.example.hexi.canvastest.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by chengxin on 3/2/15.
 */
public class DisplaySizeUtil {
    private static final String TAG = DisplaySizeUtil.class.getSimpleName();

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static int convertDpToPx(Context context, int dp){
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static int convertPxToDp(Context context, int px){
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, r.getDisplayMetrics());
    }

    public static int getScreenWidth(Context context) {
        return convertPxToDp(context, context.getResources().getDisplayMetrics().widthPixels);
    }

    public static int getScreenHeight(Context context) {
        return convertPxToDp(context, context.getResources().getDisplayMetrics().heightPixels);
    }
}
