package com.sao.mobile.sao.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sao.mobile.saolib.entities.User;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.activity.LoginActivity;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private UserManager mUserManager = UserManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Init Splash Screen");
        setStatusBarTranslucent(true);

        getKeyHash();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();

        Boolean log = true;

        if(log) {
            retrieveUserInfo();
            registerDevice();
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void retrieveUserInfo() {
        Call<User> deviceCall = mApiManager.userService.retrieveUserInfo();
        deviceCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve user info");
                    return;
                }

                Log.i(TAG, "Success retrieve user info");
                mUserManager.currentUser = response.body();
                startMainActivity();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail retrieve user info.", t);
                SnackBarUtils.showSnackError(getView());
                retrieveUserInfo();
            }
        });
    }

    private void registerDevice() {
        String deviceId = FirebaseInstanceId.getInstance().getId();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "DeviceId: " + deviceId + "DeviceToken: " + deviceToken);

        Call<Void> deviceCall = mApiManager.userService.registerDevice(deviceId, deviceToken);
        deviceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail register device");
                    return;
                }

                Log.i(TAG, "Success register device");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail register device.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    public void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sao.mobile.sao",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
            Log.e(TAG, "Key Hash: cause= " + ignored.getCause() + " message= " + ignored.getMessage());
        }
    }
}
