package com.sao.mobile.saolib.manager;

import com.sao.mobile.saolib.SaoConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by SEB on 27/02/2017.
 */
public abstract class AbstractApiManager {
    public Retrofit retrofit;

    protected AbstractApiManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(SaoConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        initServices();
    }

    protected abstract void initServices();
}
