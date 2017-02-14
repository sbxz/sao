package com.sao.mobile.sao;

import android.app.Application;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.sao.mobile.sao.entities.SaoBeacon;
import com.sao.mobile.saolib.EstimoteConstants;

import java.util.List;

/**
 * Created by Seb on 11/02/2017.
 */

public class SaoApp extends Application {
    private static final String TAG = SaoApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), EstimoteConstants.APP_ID, EstimoteConstants.APP_TOKEN);
        EstimoteSDK.enableDebugLogging(false);
    }
}