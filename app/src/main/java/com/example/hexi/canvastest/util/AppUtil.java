package com.example.hexi.canvastest.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Created by hexi on 16/4/21.
 */
public class AppUtil {

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                List<ActivityManager.RunningAppProcessInfo> appList = activityManager.getRunningAppProcesses();

                String packageName = context.getPackageName();
                for (ActivityManager.RunningAppProcessInfo appProcess : appList) {
                    if (packageName.equals(appProcess.processName)) {
                        return (appProcess.importance ==
                                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            return false;
        } else {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (!topActivity.getPackageName().equals(context.getPackageName())) {
                    return false;
                }
            }
        }

        return true;
    }
}
