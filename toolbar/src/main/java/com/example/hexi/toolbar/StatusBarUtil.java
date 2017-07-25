package com.example.hexi.toolbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by hexi on 2017/7/18.
 */

public class StatusBarUtil {

    public static void hideStatusBar(Activity activity) {
        Window window = activity.getWindow();
        if (canHideStatusBar()) {//5.0及以上
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
//            WindowManager.LayoutParams localLayoutParams = window.getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//        }
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (canHideStatusBar()) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public static boolean canHideStatusBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static int getDefaultColor(Context context) {
        return context.getResources().getColor(R.color.theme_default_primary);
    }
}
