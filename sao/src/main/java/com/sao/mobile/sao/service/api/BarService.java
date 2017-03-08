package com.sao.mobile.sao.service.api;

import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.Product;
import com.sao.mobile.saolib.entities.User;
import com.sao.mobile.saolib.entities.api.BeaconResponse;

import java.util.List;
import java.util.Map;

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
    Call<BeaconResponse> scanBeacon(@Query("facebookUserId") String facebookUserId, @Query("uuid") String uuid, @Query("major") int major, @Query("minor") int minor);

    @POST("bar/order/start")
    Call<Order> startOrder(@Body Order order);

    @GET("bar/beacon/order")
    Call<Void> orderBeacon(@Query("facebookUserId") String facebookUserId, @Query("uuid") String uuid);

    @GET("bar/beacon/leave")
    Call<Void> leaveBar(@Query("facebookUserId") String facebookUserId);

    @GET("news/")
    Call<List<News>> getNews(@Query("facebookUserId") String facebookUserId);

    @GET("bar/catalog")
    Call<Map<String, List<Product>>> retrieveCatalog(@Query("barId") Long barId);

    @GET("bar/friend")
    Call<List<User>> retrieveBarFriends(@Query("facebookUserId") String facebookUserId, @Query("barId") Long barId);

    @GET("bar/news")
    Call<List<News>> retrieveNewsBar(@Query("barId") Long barId);
}
