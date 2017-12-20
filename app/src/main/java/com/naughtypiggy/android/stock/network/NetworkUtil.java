package com.naughtypiggy.android.stock.network;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.naughtypiggy.android.stock.LoginActivity;
import com.naughtypiggy.android.stock.MainActivity;
import com.naughtypiggy.android.stock.MyApplication;
import com.naughtypiggy.android.stock.R;
import com.naughtypiggy.android.stock.network.model.ApiResp;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yangyu on 3/12/17.
 */

public class NetworkUtil{
    private static final NetworkService service_login;
    public static final NetworkService service;

    private static class TokenInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response originalResp = chain.proceed(request);

//            String responseBody = originalResp.body().string();
            if (originalResp.code() == 401) {
                if (AuthManager.loginSync()) {
                    Request newRequest = request.newBuilder().header("Authorization", AuthManager.getAccessToken()).build();
                    originalResp.body().close();
                    return chain.proceed(newRequest);
                }

//                Pair<String, String> nameAndPassword = AuthManager.getUserNameAndPassword();
//                String userName = nameAndPassword.first;
//                String password = nameAndPassword.second;
//                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
//                    String authCode = AuthManager.getAuthCode();
//                    Call<ApiResp.AuthResp> call = service_login.login(authCode, userName, password);
//                    ApiResp.AuthResp resp = call.execute().body();
//
//                    AuthManager.updateAuthResp(resp);
//
//                    Request newRequest = request.newBuilder().header("Authorization", AuthManager.getAccessToken()).build();
//
//                    originalResp.body().close();
//                    return chain.proceed(newRequest);
//
//                }
            }

            return originalResp;
        }
    }

    static {
        Context context = MyApplication.getContext();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(context.getString(R.string.server_address))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service_login = retrofit.create(NetworkService.class);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new TokenInterceptor()).build();

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        }).create();

        Retrofit retrofit1 = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        service = retrofit1.create(NetworkService.class);
    }

    public static void test(Context context) {
        Intent loginIntent = new Intent(context, LoginActivity.class);

        context.startActivity(loginIntent);
    }



}
