package com.gensee.networklistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by hexi on 16/3/19.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "NetworkReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOGTAG, "Action: " + intent.getAction());
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            String typeName = info.getTypeName();
            String subtypeName = info.getSubtypeName();
            boolean available = info.isAvailable();
            boolean isConnected = info.isConnected();
            String extraInfo = info.getExtraInfo();
            Log.i(LOGTAG, String.format("===network changed network, type:%s, " +
                    "subtype:%s, available:%b, isConnected:%b, extraInfo:%s",
                    typeName, subtypeName, available, isConnected, extraInfo));
        }
    }
}
