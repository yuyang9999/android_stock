package com.naughtypiggy.android.stock;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.naughtypiggy.android.stock.network.NetworkService;
import com.naughtypiggy.android.stock.network.model.ApiUserResp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEtUserName;
    private EditText mEtEmail;
    private EditText mEtPassword;

    private Button mSubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtUserName = (EditText)findViewById(R.id.et_register_username);
        mEtEmail = (EditText)findViewById(R.id.et_register_email);
        mEtPassword = (EditText)findViewById(R.id.et_register_password);

        mSubmitBtn = (Button)findViewById(R.id.bt_register);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mEtUserName.getText().toString();
                String email = mEtEmail.getText().toString();
                String password = mEtPassword.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "input can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.server_address))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                NetworkService service = retrofit.create(NetworkService.class);

                Call<ApiUserResp> call = service.registerUser(userName, email, password);

                call.enqueue(new Callback<ApiUserResp>() {
                    @Override
                    public void onResponse(Call<ApiUserResp> call, Response<ApiUserResp> response) {
                        System.out.println(response);
                        System.out.println(call);
                    }

                    @Override
                    public void onFailure(Call<ApiUserResp> call, Throwable t) {
                        System.out.println(t);
                    }
                });
            }
        });

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
