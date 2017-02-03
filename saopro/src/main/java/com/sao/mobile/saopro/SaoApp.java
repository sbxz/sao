package com.sao.mobile.saopro;

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;
import com.sao.mobile.saolib.EstimoteConstants;

/**
 * Created by Seb on 28/11/2016.
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
