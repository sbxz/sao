package com.sao.mobile.sao.service.api;

import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {
    @GET("device/user/register")
    Call<Void> registerDevice(@Query("deviceId") String deviceId, @Query("token") String token);

    @GET("user/")
    Call<User> retrieveUserInfo();

    @GET("bar/")
    Call<List<Bar>> retrieveBars();
}
