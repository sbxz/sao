package com.sao.mobile.saopro.service.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Seb on 28/11/2016.
 */

public interface LoginService {
    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> login();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> logout();
}
