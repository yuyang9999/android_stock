package com.naughtypiggy.android.stock.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;

import com.google.gson.Gson;
import com.naughtypiggy.android.stock.MyApplication;
import com.naughtypiggy.android.stock.R;
import com.naughtypiggy.android.stock.network.model.ApiResp;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yangyu on 3/12/17.
 */

public class AuthManager {
    static private String sUserName;
    static private String sPassword;
    static private ApiResp.AuthResp sAuthResp;
    static private Date sAuthTime;

    private AuthManager() {}

    static {
        Context context = MyApplication.getContext();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = preferences.getString(context.getString(R.string.username), null);
        String password = preferences.getString(context.getString(R.string.password), null);
        String respStr = preferences.getString(context.getString(R.string.auth_resp), null);
        String authTimeStr = preferences.getString(context.getString(R.string.auth_time), null);
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            sUserName = userName;
            sPassword = password;
        }

        if (!TextUtils.isEmpty(respStr) && !TextUtils.isEmpty(authTimeStr)) {
            Gson gson = new Gson();
            ApiResp.AuthResp resp = gson.fromJson(respStr, ApiResp.AuthResp.class);
            sAuthResp = resp;
            sAuthTime = gson.fromJson(authTimeStr, Date.class);
        }
    }

    public interface LoginCallback {
        void loginSucceed();
        void loginFailed(String msg);
    }

    public static void loginAsync(final String username, final String password, final LoginCallback callback) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            callback.loginFailed("user name or password can't be empty");
            return;
        }

        Call<ApiResp.AuthResp> call = NetworkUtil.service.login(getAuthCode(), username, password);
        call.enqueue(new Callback<ApiResp.AuthResp>() {
            @Override
            public void onResponse(Call<ApiResp.AuthResp> call, Response<ApiResp.AuthResp> response) {
                ApiResp.AuthResp resp = response.body();
                if (resp == null) {
                    callback.loginFailed("Login failed");
                } else {
                    saveAuthInfo(username, password, resp);
                    callback.loginSucceed();
                }
            }

            @Override
            public void onFailure(Call<ApiResp.AuthResp> call, Throwable t) {
                callback.loginFailed(t.getLocalizedMessage());
            }
        });
    }


    public static boolean loginSync() throws IOException {
        if (TextUtils.isEmpty(sUserName) || TextUtils.isEmpty(sPassword)) {
            return false;
        }

        Call<ApiResp.AuthResp> call = NetworkUtil.service.login(getAuthCode(), sUserName, sPassword);
        ApiResp.AuthResp resp = call.execute().body();
        if (resp == null) {
            return false;
        }

        if (!resp.equals(sAuthResp)) {
            saveAuthInfo(sUserName, sPassword, resp);
        }

        return true;
    }


    private static void saveAuthInfo(String userName, String password, ApiResp.AuthResp resp) {
        sUserName = userName;
        sPassword = password;
        sAuthResp = resp;
        sAuthTime = new Date();

        Context context = MyApplication.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.username), userName);
        editor.putString(context.getString(R.string.password), password);


        Gson gson = new Gson();
        String respStr = gson.toJson(resp);
        String authTime = gson.toJson(sAuthTime);
        editor.putString(context.getString(R.string.auth_resp), respStr);
        editor.putString(context.getString(R.string.auth_time), authTime);
        editor.apply();
    }

    public static String getAccessToken() {
        if (sAuthResp == null) {
            return "";
        }
        return "Bearer " + sAuthResp.access_token;
    }

    public static void logout() {
        Context context = MyApplication.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(context.getString(R.string.auth_resp), "");
        editor.putString(context.getString(R.string.auth_time), "");
        editor.putString(context.getString(R.string.auth_resp), "");
        editor.apply();

        sUserName = "";
        sPassword = "";
        sAuthResp = null;
    }


    private static String getAuthCode() {
        String authCode = new String(Base64.encode("fooClientIdPassword:secret".getBytes(), Base64.DEFAULT));
        authCode = authCode.substring(0, authCode.length() - 1);
        authCode = "Basic " + authCode;

        return authCode;
    }


    public static boolean isAlreadyLoggedIn() {
        if (TextUtils.isEmpty(getAccessToken())) {
            return false;
        }

        if (sAuthTime == null) {
            return false;
        }

        Date curDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(sAuthTime);
        cal.add(Calendar.SECOND, sAuthResp.expires_in);
        Date expireDate = cal.getTime();

        return curDate.before(expireDate);
    }

}
