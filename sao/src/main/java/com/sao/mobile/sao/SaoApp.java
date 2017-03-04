package com.sao.mobile.sao;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.estimote.sdk.EstimoteSDK;
import com.sao.mobile.saolib.EstimoteConstants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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