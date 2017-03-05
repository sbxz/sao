package com.sao.mobile.saopro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Trader;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;
import com.sao.mobile.saopro.ui.activity.BarSelectActivity;
import com.sao.mobile.saopro.ui.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Seb on 08/11/2016.
 */
public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Init Splash Screen");
        setStatusBarTranslucent(true);

        String traderId = mTraderManager.getTraderId(mContext);

        if(traderId != null) {
            retrieveTraderInfo();
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

    private void retrieveTraderInfo() {
        String traderId = mTraderManager.getTraderId(mContext);
        if(traderId == null) {
            startLoginActivity();
            return;
        }

        Call<Trader> traderCall = mApiManager.traderService.retrieveTraderInfo(traderId);
        traderCall.enqueue(new Callback<Trader>() {
            @Override
            public void onResponse(Call<Trader> call, Response<Trader> response) {
                Log.i(TAG, "Success retrieve user info");
                mTraderManager.trader = response.body();
                String barId = mTraderManager.getBarId(mContext);

                if(barId == null) {
                    startActivity(BarSelectActivity.class);
                }

                for(Bar bar : mTraderManager.trader.getBars()) {
                    if(bar.getBarId().toString().equals(barId)) {
                        mTraderManager.currentBar = bar;
                        break;
                    }
                }

                startMainActivity();
            }

            @Override
            public void onFailure(Call<Trader> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail retrieve user info.", t);
                SnackBarUtils.showSnackError(getView());
                retrieveTraderInfo();
            }
        });
    }
}
