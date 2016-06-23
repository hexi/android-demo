package com.vitamio.networklistener;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by chengxin on 3/2/15.
 */
public class NetworkUtil {

    public static boolean isNetworkConnected(Context context) {
        boolean ret = true;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                ret = false;
            } else {
                NetworkInfo networkinfo = manager.getActiveNetworkInfo();
                if (networkinfo == null || !networkinfo.isAvailable() || !networkinfo.isConnected()) {
                    ret = false;
                } else {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    boolean b = wifiManager.pingSupplicant();//ping网络是否能够连通。
                    Log.d("network", "当前网络是否可以正常浏览网页：" + b);
                    ret = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

}
