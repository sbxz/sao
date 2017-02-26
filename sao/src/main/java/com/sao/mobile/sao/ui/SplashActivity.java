package com.sao.mobile.sao.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.User;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.api.UserService;
import com.sao.mobile.sao.ui.activity.LoginActivity;
import com.sao.mobile.saolib.service.SaoMobileService;
import com.sao.mobile.saolib.ui.base.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private UserService mUserService;

    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void initServices() {
        mUserService = retrofit.create(UserService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Init Splash Screen");
        setStatusBarTranslucent(true);

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
        Call<User> deviceCall = mUserService.retrieveUserInfo();
        deviceCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i(TAG, "Success retrieve user info");
                mUserManager.currentUser = response.body();
                startMainActivity();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Fail retrieve user info. Message= " + t.getMessage());
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                retrieveUserInfo();
            }
        });
    }

    private void registerDevice() {
        String deviceId = FirebaseInstanceId.getInstance().getId();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "DeviceId: " + deviceId + "DeviceToken: " + deviceToken);

        Call<Void> deviceCall = mUserService.registerDevice(deviceId, deviceToken);
        deviceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success register device");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail register device. Message= " + t.getMessage());
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
