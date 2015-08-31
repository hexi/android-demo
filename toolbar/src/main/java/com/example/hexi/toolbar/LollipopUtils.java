package com.example.hexi.toolbar;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by hexi on 15/8/31.
 */
public class LollipopUtils {

    public static int getStatusBarHeight(Context context) {
        Context appContext = context.getApplicationContext();
        int result = 0;
        int resourceId =
                appContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        Log.d("ScreenUtils", result + "");
        return result;
    }

    public static void setStatusbarColor(Activity activity, View view) {

        //对于Lollipop 的设备，只需要在style.xml中设置colorPrimaryDark即可

        //对于4.4的设备，如下设置padding即可，颜色同样在style.xml中配置
        Window w = activity.getWindow();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = getStatusBarHeight(activity);
//            view.setPadding(0, statusBarHeight, 0, 0);
            return;
        }
    }

}
