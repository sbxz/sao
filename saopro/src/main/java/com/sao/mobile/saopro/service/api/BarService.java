package com.sao.mobile.saopro.service.api;

import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saopro.entities.TraderOrder;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Seb on 01/12/2016.
 */

public interface BarService {

    @GET("bar/order")
    Call<Map<String, List<TraderOrder>>> retrieveOrderList(@Query("barId") Long barId);

    @GET("bar/order/confirm")
    Call<Void> confirmOrder(@Query("orderId") Long orderId);

    @GET("bar/order/validate")
    Call<Void> validateOrder(@Query("orderId") Long orderId);

    @POST("bar/beacon")
    Call<Void> associateBeacon(@Query("barId") Long barId, @Body SaoBeacon saoBeacon);

    @GET("bar/beacon")
    Call<List<SaoBeacon>> retrieveBeaconBar(@Query("barId") Long barId);

    @POST("news/")
    Call<Void> sendNotification(@Body News news);

    @GET("bar/beacon/forOrder")
    Call<Void> beaconForOrder(@Query("barId") Long barId, @Query("uuid") String uuid, @Query("forOrder") Boolean forOrder);

    @GET("bar/beacon/enable")
    Call<Void> beaconEnable(@Query("barId") Long barId, @Query("uuid") String uuid, @Query("enable") Boolean enable);

    @GET("bar/beacon/delete")
    Call<Void> deleteBeacon(@Query("barId") Long barId, @Query("uuid") String uuid);
}
