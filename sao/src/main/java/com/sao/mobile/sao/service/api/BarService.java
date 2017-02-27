package com.sao.mobile.sao.service.api;

import com.sao.mobile.sao.entities.News;
import com.sao.mobile.sao.entities.Order;
import com.sao.mobile.sao.entities.api.BeaconResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Seb on 11/02/2017.
 */

public interface BarService {
    @GET("bar/beacon/scan")
    Call<BeaconResponse> scanBeacon(@Query("uuid") String uuid, @Query("major") int major, @Query("minor") int minor);

    @POST("bar/order/start")
    Call<Order> startOrder(@Body Order order);

    @GET("bar/beacon/leave")
    Call<Void> leaveBar(@Query("barId") Long barId);

    @GET("news/")
    Call<List<News>> getNews();
}
