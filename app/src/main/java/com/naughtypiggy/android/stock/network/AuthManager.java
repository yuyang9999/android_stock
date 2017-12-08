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

/**
 * Created by yangyu on 3/12/17.
 */

public class AuthManager {
    static private String sUserName;
    static private String sPassword;
    static private ApiResp.AuthResp sAuthResp;

    static private final String userNameKey;
    static private final String passwordKey;
    static private final String respKey;

    private AuthManager() {}

    static {
        Context context = MyApplication.getContext();
        userNameKey = context.getString(R.string.username);
        passwordKey = context.getString(R.string.password);
        respKey = context.getString(R.string.auth_resp);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = preferences.getString(userNameKey, null);
        String password = preferences.getString(passwordKey, null);
        String respStr = preferences.getString(respKey, null);
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(respStr)) {
            sUserName = userName;
            sPassword = password;
            Gson gson = new Gson();
            ApiResp.AuthResp resp = gson.fromJson(respStr, ApiResp.AuthResp.class);
            sAuthResp = resp;
        }

    }


    public static void updateAuthResp(ApiResp.AuthResp resp) {
        sAuthResp = resp;
//
//        Context context = MyApplication.getContext();
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        Gson gson = new Gson();
//        String respStr = gson.toJson(resp);
//        editor.putString("AuthResp", respStr);
//        editor.apply();
    }

    public static void saveAuthInfo(String userName, String password, ApiResp.AuthResp resp) {
        sUserName = userName;
        sPassword = password;
        sAuthResp = resp;

        Context context = MyApplication.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.username), userName);
        editor.putString(context.getString(R.string.password), password);

        Gson gson = new Gson();
        String respStr = gson.toJson(resp);
        editor.putString(context.getString(R.string.auth_resp), respStr);
        editor.apply();
    }

    private Pair<String, String> getAuthUserNameAndPassword(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = preferences.getString(context.getString(R.string.username), "");
        String password = preferences.getString(context.getString(R.string.password), "");

        return new Pair<>(username, password);
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
        editor.apply();
    }

    public static Pair<String, String> getUserNameAndPassword() {
        return new Pair<String, String>("tom", "111111");
    }

    public static String getAuthCode() {
        String authCode = new String(Base64.encode("fooClientIdPassword:secret".getBytes(), Base64.DEFAULT));
        authCode = authCode.substring(0, authCode.length() - 1);
        authCode = "Basic " + authCode;

        return authCode;
    }


    public static boolean isAlreadyLoggedIn() {
        return TextUtils.isEmpty(getAccessToken()) == false;
    }

}
