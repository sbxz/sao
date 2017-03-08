package com.sao.mobile.sao.service.api;

import com.sao.mobile.sao.entities.FacebookResult;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.User;
import com.sao.mobile.saolib.entities.api.FriendBar;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @POST("user/login")
    Call<Void> login(@Body FacebookResult facebookResult, @Query("deviceId") String deviceId, @Query("token") String token);

    @GET("user/logout")
    Call<Void> logout(@Query("facebookUserId") String facebookUserId);

    @GET("device/user/register")
    Call<Void> registerDevice(@Query("facebookUserId") String facebookUserId, @Query("deviceId") String deviceId, @Query("token") String token);

    @GET("user/")
    Call<User> retrieveUserInfo(@Query("facebookUserId") String facebookUserId);

    @GET("bar/")
    Call<List<Bar>> retrieveBars(@Query("facebookUserId") String facebookUserId);

    @GET("user/friend")
    Call<List<FriendBar>> retrieveFriendBar(@Query("facebookUserId") String facebookUserId);

    @GET("user/currentOrder")
    Call<Order> getCurrentOrder(@Query("facebookUserId") String facebookUserId);
}
