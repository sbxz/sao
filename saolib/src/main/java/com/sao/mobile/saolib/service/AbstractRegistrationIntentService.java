package com.sao.mobile.saolib.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.sao.mobile.saolib.SaoConstants;

/**
 * Created by Seb on 12/08/2016.
 */
public class AbstractRegistrationIntentService extends IntentService{
    private static final String TAG = "RegIntentService";

    public AbstractRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String resultString = null;
        String regID = null;
        String storedToken = null;

        try {
            String FCM_token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "FCM Registration Token: " + FCM_token);

            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.

            regID = sharedPreferences.getString("registrationID", null);
            storedToken = sharedPreferences.getString("FCMtoken", "");

            if(regID == null){
                NotificationHub hub = new NotificationHub(SaoConstants.SaoHubName,
                        SaoConstants.SaoHubListenConnectionString, this);
                Log.d(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token);
                regID = hub.register(FCM_token).getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.d(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply();
            }

            // Check if the token may have been compromised and needs refreshing.
            else if(storedToken != FCM_token) {
                NotificationHub hub = new NotificationHub(SaoConstants.SaoHubName,
                        SaoConstants.SaoHubListenConnectionString, this);
                Log.d(TAG, "NH Registration refreshing with token : " + FCM_token);
                regID = hub.register(FCM_token).getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.d(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID ).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token ).apply();
            }

            else {
                Log.i(TAG, "Previously Registered Successfully - RegId : " + regID);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete registration", e);
        }
    }
}
