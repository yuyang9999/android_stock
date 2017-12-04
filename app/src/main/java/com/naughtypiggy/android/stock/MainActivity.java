package com.naughtypiggy.android.stock;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.naughtypiggy.android.stock.network.NetworkUtil.service;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mButton;

    MyApplication mMyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this.getApplicationContext();
        System.out.println(context);

        mButton = (Button) findViewById(R.id.testButton);
        mButton.setOnClickListener(this);

        AuthManager manager = AuthManager.getDefaultManager();

        manager.startAuth("tom", "111111", new AuthManager.AuthConfig() {
            @Override
            public void finishHandler() {
                String accessToken = AuthManager.getAccessToken(MainActivity.this);

                Call<ApiResp.ApiProfileResp> call = NetworkUtil.service.getProfiles(accessToken);
                call.enqueue(new Callback<ApiResp.ApiProfileResp>() {
                    @Override
                    public void onResponse(Call<ApiResp.ApiProfileResp> call, Response<ApiResp.ApiProfileResp> response) {
                        System.out.println(response);
                    }

                    @Override
                    public void onFailure(Call<ApiResp.ApiProfileResp> call, Throwable t) {
                        System.out.println(t);
                    }
                });
            }

            @Override
            public void startHandler() {
                System.out.println("before");
            }
        }, this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.testButton) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
