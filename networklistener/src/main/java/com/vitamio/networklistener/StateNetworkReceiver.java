package com.vitamio.networklistener;

import android.content.Context;

/**
 * Created by linrizhao on 16/5/24.
 */
public class StateNetworkReceiver extends NetworkReceiver {
    private boolean lastConnectedState;

    public StateNetworkReceiver(Context context, NetworkReceiver.onNetworkChangedListener onNetworkChangedListener) {
        super(onNetworkChangedListener);
        lastConnectedState = NetworkUtil.isNetworkConnected(context.getApplicationContext());
    }

    @Override
    protected void fireNetworkChanged(int type, boolean isConnected) {
        if (isConnected == lastConnectedState) {
            return;
        }
        this.lastConnectedState = isConnected;
        super.fireNetworkChanged(type, isConnected);
    }
}
