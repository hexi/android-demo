package com.example.hexi.canvastest.view;

import com.example.hexi.canvastest.DigestRunnable;

/**
 * Created by hexi on 15/11/20.
 */
public class TestDigestRunnable {

    public static void main(String[] args) {

        new Thread(new DigestRunnable("/Users/hexi/Documents/apk/cocos2d-chart/ytxmobile-debug.apk"))
                .start();
    }
}
