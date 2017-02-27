package com.sao.mobile.saopro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.entities.Bar;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.UserManager;
import com.sao.mobile.saopro.ui.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Seb on 08/11/2016.
 */
public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    private ApiManager mApiManager = ApiManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Init Splash Screen");
        setStatusBarTranslucent(true);

        Boolean log = true;

        if(log) {
            retrieveTraderInfo();
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
        finish();
    }

    private void retrieveTraderInfo() {
        Call<Void> traderCall = mApiManager.traderService.retrieveTraderInfo();
        traderCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success retrieve user info");
                List<Bar> bars = parseData();
                mUserManager.bars = bars;
                mUserManager.currentBar = bars.get(0);
                startMainActivity();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail retrieve user info.", t);
                SnackBarUtils.showSnackError(getView());
                retrieveTraderInfo();
            }
        });
    }

    private void registerDevice() {
        String deviceId = FirebaseInstanceId.getInstance().getId();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "DeviceId: " + deviceId + "DeviceToken: " + deviceToken);

        Call<Void> deviceCall = mApiManager.traderService.registerDevice();
        deviceCall.enqueue(new Callback<Void>() {
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success register device");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail register device.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private List<Bar> parseData() {
        List<Bar> bars = new ArrayList<>();
        bars.add(new Bar("1", "Red house", "http://i.imgur.com/CqmBjo5.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));
        bars.add(new Bar("2", "BreakBar", "http://i.imgur.com/zkaAooq.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));
        bars.add(new Bar("3", "La Kolok", "http://i.imgur.com/0gqnEaY.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));

        return bars;
    }
}
