package com.sao.mobile.saolib.ui.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sao.mobile.saolib.SaoConstants;

import retrofit2.Retrofit;

/**
 * Created by Seb on 11/02/2017.
 */

public abstract class BaseService extends Service {

    public Retrofit retrofit;

    public void onCreate() {
        super.onCreate();

        initRetrofit();
    }

    public void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(SaoConstants.BASE_URL)
                .build();

        initServices();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected abstract void initServices();
}
