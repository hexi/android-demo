package com.example.hexi.canvastest.util;

import android.content.res.Resources;

/**
 * Created by hexi on 16/4/19.
 */
public class DeviceUtil {

    public static float dp2px(Resources resources, float dp) {
        float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5F;
    }

    public static float sp2px(Resources resources, float sp) {
        float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale + 0.5F;
    }

}
