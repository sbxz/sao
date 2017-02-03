package com.sao.mobile.sao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sao.mobile.saolib.utils.NetworkUtils;

/**
 * Created by Seb on 27/08/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtils.getConnectivityStatus(context);

        Log.i("Receiver ", "" + status);

        if (status == NetworkUtils.TYPE_WIFI) {
        } else if (status == NetworkUtils.TYPE_MOBILE) {
        } else if (status == NetworkUtils.TYPE_NOT_CONNECTED) {
        }
    }
}
