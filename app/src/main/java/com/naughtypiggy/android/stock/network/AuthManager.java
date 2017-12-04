package com.naughtypiggy.android.stock.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;

import com.google.gson.Gson;
import com.naughtypiggy.android.stock.MyApplication;
import com.naughtypiggy.android.stock.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yangyu on 3/12/17.
 */

public class AuthManager {
    static private AuthManager sManager = null;

    private AuthManager() {}

    public static AuthManager getDefaultManager() {
        if (sManager == null) {
            sManager = new AuthManager();
        }

        return sManager;
    }

    static public class AuthResp {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
    }



    public interface AuthConfig {
        void finishHandler();
        void startHandler();
    }


    public boolean startAuth(String userName, String password, final AuthConfig config, Context context) {
        class AuthTask extends AsyncTask<URL, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                config.startHandler();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                config.finishHandler();
            }

            @Override
            protected String doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection connection = null;

                String authCode = new String(Base64.encode("fooClientIdPassword:secret".getBytes(), Base64.DEFAULT));
                authCode = authCode.substring(0, authCode.length() - 1);
                authCode = "Basic " + authCode;


                InputStreamReader in = null;
                String result = null;
                try {
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
                    connection.setRequestProperty("Authorization", authCode);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.connect();

                    in = new InputStreamReader(connection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(in);
                    StringBuffer strBuffer = new StringBuffer();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        strBuffer.append(line);
                    }

                    result = strBuffer.toString();
                    in.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                return result;
            }
        }

        String result = null;
        try {
            URL url = new URL("http://10.0.2.2:8080/oauth/token?grant_type=password&username=" + userName + "&password=" + password);
            AuthTask task = new AuthTask();
            task.execute(url);
            result = task.get();

            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AuthResp resp = null;
        if (result != null) {
            Gson gson = new Gson();
            resp = gson.fromJson(result, AuthResp.class);
            saveAuthInfo(userName, password, resp, context);
        }

        return resp != null;
    }

    private void saveAuthInfo(String userName, String password, AuthResp resp, Context context) {
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

    public static String getAccessToken(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String respStr = preferences.getString(context.getString(R.string.auth_resp), "");
        if (TextUtils.isEmpty(respStr)) {
            return null;
        }

        Gson gson = new Gson();
        AuthResp resp = gson.fromJson(respStr, AuthResp.class);
        return "Bearer " + resp.access_token;
    }

}
