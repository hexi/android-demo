package com.example.hexi.canvastest;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hexi on 16/1/13.
 */
public class IntDefTest {
    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_LIST, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode{}

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_LIST = 1;
    public static final int NAVIGATION_MODE_TABS = 2;

    @NavigationMode
    public int getNavigationMode() {
        return NAVIGATION_MODE_STANDARD;
    }

    public void setNavigationMode(@NavigationMode int mode) {

    }

    public void initNavigationMode() {
        setNavigationMode(NAVIGATION_MODE_LIST);
    }
}
