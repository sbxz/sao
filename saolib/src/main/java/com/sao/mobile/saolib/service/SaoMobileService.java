package com.sao.mobile.saolib.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

/**
 * Created by Seb on 11/08/2016.
 */
public class SaoMobileService {
    private static final String TAG = "SaoMobileService";

    public static final String SHARED_PREF_FILE = "temp";
    public static final String USER_ID_PREF = "uid";
    public static final String TOKEN_PREF = "token";

    public static MobileServiceClient client = null;

    public static void authenticate(final Context context, MobileServiceAuthenticationProvider mobileServiceAuthenticationProvider) {
        client.setContext(context);

        ListenableFuture<MobileServiceUser> mLogin = client.login(mobileServiceAuthenticationProvider);

        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {
                Log.e(TAG, "Error");
            }
            @Override
            public void onSuccess(MobileServiceUser user) {
                Log.i(TAG, "Success login");
                cacheUserToken(context, user);
            }
        });
    }

    public static void cacheUserToken(Context context, MobileServiceUser user) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_ID_PREF, user.getUserId());
        editor.putString(TOKEN_PREF, user.getAuthenticationToken());
        editor.commit();
    }

    public static boolean loadUserTokenCache(Context context) {
        if(client == null) {
            return false;
        }

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USER_ID_PREF, null);

        if (userId == null) {
            return false;
        }

        String token = prefs.getString(TOKEN_PREF, null);

        if (token == null) {
            return false;
        }

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }
}
