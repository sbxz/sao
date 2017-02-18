package com.sao.mobile.sao.service.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Seb on 11/02/2017.
 */

public interface BarService {
    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> detectBar();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> launchTraderOrder();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> leaveBar();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> retrieveNewsInfo();
}
