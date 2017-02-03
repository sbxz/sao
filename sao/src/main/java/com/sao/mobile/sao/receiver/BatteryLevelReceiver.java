package com.sao.mobile.sao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Seb on 27/08/2016.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action == Intent.ACTION_BATTERY_LOW) {

        } else if(action == Intent.ACTION_BATTERY_OKAY) {

        }
    }
}
