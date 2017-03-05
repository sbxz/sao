package com.sao.mobile.saopro.service.api;

import com.sao.mobile.saolib.entities.Trader;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Seb on 05/01/2017.
 */

public interface TraderService {
    @GET("trader/login")
    Call<Trader> login(@Query("mail") String mail, @Query("password") String password);

    @GET("trader/logout")
    Call<Void> logout(@Query("traderId") String traderId);

    @GET("trader/info")
    Call<Trader> retrieveTraderInfo(@Query("traderId") String traderId);

    @GET("device/trader/register")
    Call<Void> registerDevice(@Query("barId") Long barId, @Query("deviceId") String deviceId, @Query("token") String token);

}
