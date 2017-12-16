package com.naughtypiggy.android.stock;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.naughtypiggy.android.stock.network.model.ProfileStock;
import com.naughtypiggy.android.stock.utility.Utility;

public class ProfileStockActivity extends AppCompatActivity {

    private ProfileStock mStock;
    private static final int OPEN_SEARCH_ACTIVITY_ITEM = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_stock);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String stockString = intent.getStringExtra(getString(R.string.one_stock_profile_key));
        mStock = (ProfileStock) Utility.ungsonObject(stockString, ProfileStock.class);
    }
}
