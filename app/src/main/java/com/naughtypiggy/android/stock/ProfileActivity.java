package com.naughtypiggy.android.stock;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.Profile;
import com.naughtypiggy.android.stock.network.model.ProfileStock;
import com.naughtypiggy.android.stock.utility.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private Profile mProfile;
    private List<ProfileStock> mStocks;

    RecyclerView mStockListView;

    private static class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StocksViewHolder> {

        private List<ProfileStock> stocks;
        private ClickListener listener;

        public interface ClickListener {
            void clicked(int idx);
        }

        public StocksAdapter (List<ProfileStock> stocks, ClickListener listener) {
            this.stocks = stocks;
            this.listener = listener;
        }

        @Override
        public StocksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            int layoutItem = R.layout.profile_stock_list_item;
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(layoutItem, parent, false);
            StocksViewHolder viewHolder = new StocksViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(StocksViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return stocks.size();
        }

        public class StocksViewHolder extends RecyclerView.ViewHolder {
            private TextView mTextView;

            public StocksViewHolder(View itemView) {
                super(itemView);

                mTextView = (TextView) itemView.findViewById(R.id.tv_profile_stock);
            }


            public void bind(int position) {
                ProfileStock stock = stocks.get(position);
                mTextView.setText(stock.getSname());
            }
        }
    }


    private void refreshProfileStocks () {
        Call<ApiResp.ApiProfileSymbolResp> call = NetworkUtil.service.getProfileSymbols(AuthManager.getAccessToken(), mProfile.getPname());
        call.enqueue(new Callback<ApiResp.ApiProfileSymbolResp>() {
            @Override
            public void onResponse(Call<ApiResp.ApiProfileSymbolResp> call, Response<ApiResp.ApiProfileSymbolResp> response) {
                ApiResp.ApiProfileSymbolResp resp = response.body();
                if (resp != null && !resp.hasError) {
                    mStocks = response.body().response;

                    StocksAdapter adapter = new StocksAdapter(mStocks, new StocksAdapter.ClickListener() {
                        @Override
                        public void clicked(int idx) {
                            ProfileStock stock = mStocks.get(idx);
                            String stockString = Utility.gsonObject(stock);
                            Intent intent = new Intent(ProfileActivity.this, ProfileStockActivity.class);
                            intent.putExtra(getString(R.string.one_stock_profile_key), stockString);
                            startActivity(intent);
                        }
                    });

                    mStockListView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ApiResp.ApiProfileSymbolResp> call, Throwable t) {
                Log.e(ProfileActivity.class.getName(), t.getLocalizedMessage());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String profileString = intent.getStringExtra(getString(R.string.one_profile_key));
        mProfile = (Profile) Utility.ungsonObject(profileString, Profile.class);

        mStockListView = (RecyclerView) findViewById(R.id.rv_profileStockList);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mStockListView.setLayoutManager(manager);
        mStockListView.setHasFixedSize(true);

        setTitle("Stocks");
    }



    @Override
    protected void onResume() {
        super.onResume();
        refreshProfileStocks();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
