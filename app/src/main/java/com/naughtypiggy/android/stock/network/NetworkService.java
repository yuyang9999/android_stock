package com.naughtypiggy.android.stock.network;

import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.ApiUserResp;
import com.naughtypiggy.android.stock.network.model.StockQueryInfo;

import java.util.List;

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

    @GET("/api/profile_add")
    Call<ApiUserResp> createNewProfile(@Header("Authorization") String authorization, @Query("pname") String pname);

    @POST("/api/profile_delete")
    Call<ApiResp.ApiBooleanResp> deleteProfile(@Header("Authorization") String authroization, @Query("pname") String pname);

    @GET("/api/profile_symbols")
    Call<ApiResp.ApiProfileSymbolResp> getProfileSymbols(@Header("Authorization") String authorization, @Query("pname") String profileName);

    @GET("/api/profile_symbol_add")
    Call<ApiResp.ApiBooleanResp> addProfileSymbol(@Header("Authorization") String authorization, @Query("pname") String profileName,
                                                  @Query("sname") String stockName, @Query("share") int shares, @Query("price") float price,
                                                  @Query("bought_date") String date);

    @GET("/api/queryStocks")
    Call<List<StockQueryInfo>> queryStockSymbol(@Header("Authorization") String authroization, @Query("symbol") String querySymbol);


    @GET("/api/profile_only_add_symbol")
    Call<ApiResp.ApiBooleanResp> addStockSymbol(@Header("Authorization") String authorization, @Query("pname") String pname, @Query("sname")String symbolName);

    @POST("/api/profile_symbol_delete")
    Call<ApiResp.ApiBooleanResp> deleteProfileStock(@Header("Authorization") String authorization, @Query("pname") String pname, @Query("profile_stock_id") int profile_stock_id);

}
