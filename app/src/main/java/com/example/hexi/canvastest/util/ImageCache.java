package com.example.hexi.canvastest.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

/**
 * Created by hexi on 15/12/14.
 */
public class ImageCache {

    private volatile static SparseArray<WeakReference<Drawable>> drawableCache = new SparseArray<>();
    public static Drawable getImage(Context context, @DrawableRes int imageId) {
        Drawable drawable;
        if (notContain(imageId)) {
            synchronized (drawableCache) {
                if (notContain(imageId)) {
                    drawable = context.getResources().getDrawable(imageId);
                    drawableCache.put(imageId, new WeakReference<Drawable>(drawable));
                } else {
                    drawable = drawableCache.get(imageId).get();
                }
            }
        } else {
            drawable = drawableCache.get(imageId).get();
        }
        return drawable;
    }

    private static boolean notContain(int imageId) {
        return drawableCache.get(imageId) == null || drawableCache.get(imageId).get() == null;
    }
}
