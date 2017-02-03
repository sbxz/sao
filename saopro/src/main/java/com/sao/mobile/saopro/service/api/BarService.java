package com.sao.mobile.saopro.service.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Seb on 01/12/2016.
 */

public interface BarService {
    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> retrieveTraderBars();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> retrieveOrderList();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> confirmOrder();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> finishOrder();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> associateBeacon();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> retrieveBeaconBar();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> sendNotification();
}
