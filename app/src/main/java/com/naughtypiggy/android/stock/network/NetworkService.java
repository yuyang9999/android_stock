package com.naughtypiggy.android.stock.network;

import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.ApiUserResp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by yangyu on 4/12/17.
 */
public interface NetworkService {
    @Headers({
            "Content-type:application/x-www-form-urlencoded; charset=utf-8"
    })
    @POST("/oauth/token?grant_type=password")
    Call<ApiResp.AuthResp> login(@Header("Authorization") String auth, @Query("username") String userName, @Query("password") String password);


    @GET("/users/register")
    Call<ApiUserResp> registerUser(@Query("userName") String username, @Query("email") String email,
                                   @Query("password") String password);

    @GET("/api/profiles")
    Call<ApiResp.ApiProfileResp> getProfiles(@Header("Authorization") String authorization);
}
