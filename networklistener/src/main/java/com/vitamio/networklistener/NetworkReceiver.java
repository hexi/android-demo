package com.vitamio.networklistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by hexi on 16/3/19.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "NetworkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOGTAG, "Action: " + intent.getAction() + ", thread:"+Thread.currentThread());
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            String typeName = info.getTypeName();
//            String subtypeName = info.getSubtypeName();
//            boolean available = info.isAvailable();
//            boolean isConnected = info.isConnected();
//            String extraInfo = info.getExtraInfo();
//            Log.i(LOGTAG, String.format("===network changed network, type:%s, " +
//                    "subtype:%s, available:%b, isConnected:%b, extraInfo:%s",
//                    typeName, subtypeName, available, isConnected, extraInfo));


            int type = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
            String typeName = type == ConnectivityManager.TYPE_WIFI ? "WIFI" : "mobile";
            boolean isConnected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String extraInfo = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
            Log.i(LOGTAG, String.format("===network changed network, type:%s, " +
                            "isConnected:%b, extraInfo:%s, networkConnected:%b",
                    typeName, isConnected, extraInfo, NetworkUtil.isNetworkConnected(context)));
        }
    }
}
