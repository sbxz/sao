package com.sao.mobile.saopro.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sao.mobile.saopro.ui.MainActivity;

/**
 * Created by Seb on 04/01/2017.
 */

public class SaoFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = SaoFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);
    }
}
