package com.sao.mobile.saopro.service.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Seb on 05/01/2017.
 */

public interface UserService {
    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> registerDevice();

    @Headers("ZUMO-API-VERSION: 2.0.0")
    @GET("api/values")
    Call<Void> retrieveUserInfo();
}
