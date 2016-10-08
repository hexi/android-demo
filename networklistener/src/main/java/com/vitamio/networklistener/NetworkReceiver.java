package com.vitamio.networklistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by hexi on 16/3/19.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "NetworkReceiver";

    public interface onNetworkChangedListener {
        void onNetworkChanged(int type, boolean isConnected);
    }

    protected onNetworkChangedListener onNetworkChangedListener;

    public NetworkReceiver(NetworkReceiver.onNetworkChangedListener onNetworkChangedListener) {
        this.onNetworkChangedListener = onNetworkChangedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOGTAG, "Action: " + intent.getAction());
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int type = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
            NetworkInfo networkInfo = ((ConnectivityManager) context
                    .getSystemService( Context.CONNECTIVITY_SERVICE ))
                    .getNetworkInfo(type);

            boolean isConnected = networkInfo == null ? false : networkInfo.isConnected();

            String typeName = type == ConnectivityManager.TYPE_WIFI ? "WIFI" : "mobile";
            Log.i(LOGTAG, String.format("===network changed network, type:%s, " +
                            "isEffective:%b",
                    typeName, isConnected));

            fireNetworkChanged(type, isConnected);
        }
    }

    protected void fireNetworkChanged(int type, boolean isConnected) {
        if (onNetworkChangedListener != null) {
            onNetworkChangedListener.onNetworkChanged(type, isConnected);
        }
    }

    @NonNull
    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return filter;
    }

    public void removeListener() {
        this.onNetworkChangedListener = null;
    }
}
