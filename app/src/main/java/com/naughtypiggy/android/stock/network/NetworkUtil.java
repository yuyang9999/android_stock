package com.naughtypiggy.android.stock.network;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.naughtypiggy.android.stock.LoginActivity;
import com.naughtypiggy.android.stock.MainActivity;
import com.naughtypiggy.android.stock.MyApplication;
import com.naughtypiggy.android.stock.R;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yangyu on 3/12/17.
 */

public class NetworkUtil{
    public static final NetworkService service;

    static {
        Context context = MyApplication.getContext();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(context.getString(R.string.server_address))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(NetworkService.class);
    }


    public static void test(Context context) {
        Intent loginIntent = new Intent(context, LoginActivity.class);

        context.startActivity(loginIntent);
    }



}
