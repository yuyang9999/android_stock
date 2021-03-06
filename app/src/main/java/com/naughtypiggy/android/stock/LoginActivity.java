package com.naughtypiggy.android.stock;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.utility.Utility;

public class LoginActivity extends AppCompatActivity {
    Button mLoginBtn;
    Button mRegisterBtn;
    EditText mEtUserName;
    EditText mEtPassword;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Login");

        mLoginBtn = (Button) findViewById(R.id.bt_login);
        mEtUserName = (EditText) findViewById(R.id.et_username);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRegisterBtn = (Button) findViewById(R.id.bt_register);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if user name or password is valid
                String userName = mEtUserName.getText().toString();
                String password = mEtPassword.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(getApplicationContext(), "user name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthManager.loginAsync(userName, password, new AuthManager.LoginCallback() {
                    @Override
                    public void loginSucceed() {
                        onBackPressed();
                    }

                    @Override
                    public void loginFailed(String msg) {
                        Utility.showToastText(LoginActivity.this, msg);
                    }
                });
//
//                AuthManager manager = AuthManager.getDefaultManager();
//
//                boolean succeed = manager.startAuth(userName, password, new AuthManager.AuthConfig() {
//                    @Override
//                    public void finishHandler() {
//                        mProgressBar.setVisibility(View.INVISIBLE);
//                    }
//
//                    @Override
//                    public void startHandler() {
//                        mProgressBar.setVisibility(View.VISIBLE);
//                    }
//                }, LoginActivity.this);
//
//                if (!succeed) {
//                    Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
//                } else {
//                    onBackPressed();
//                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
