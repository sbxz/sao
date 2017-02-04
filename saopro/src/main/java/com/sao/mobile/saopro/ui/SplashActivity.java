package com.sao.mobile.saopro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Bar;
import com.sao.mobile.saopro.receiver.UserManager;
import com.sao.mobile.saopro.service.api.LoginService;
import com.sao.mobile.saopro.service.api.UserService;
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

    private UserManager mUserManager = UserManager.getInstance();

    private UserService mUserService;

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
        finish();
    }

    private void retrieveUserInfo() {
        Call<Void> deviceCall = mUserService.retrieveUserInfo();
        deviceCall.enqueue(new Callback<Void>() {
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
                Log.e(TAG, "Fail retrieve user info. Message= " + t.getMessage());
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void registerDevice() {
        String deviceId = FirebaseInstanceId.getInstance().getId();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "DeviceId: " + deviceId + "DeviceToken: " + deviceToken);

        Call<Void> deviceCall = mUserService.registerDevice();
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

    private List<Bar> parseData() {
        List<Bar> bars = new ArrayList<>();
        bars.add(new Bar("1", "Red house", "http://i.imgur.com/CqmBjo5.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));
        bars.add(new Bar("2", "BreakBar", "http://i.imgur.com/zkaAooq.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));
        bars.add(new Bar("3", "La Kolok", "http://i.imgur.com/0gqnEaY.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));

        return bars;
    }
}
