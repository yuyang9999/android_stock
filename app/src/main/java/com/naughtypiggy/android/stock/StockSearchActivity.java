package com.naughtypiggy.android.stock;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.Profile;
import com.naughtypiggy.android.stock.network.model.StockQueryInfo;
import com.naughtypiggy.android.stock.network.model.StockSymbol;
import com.naughtypiggy.android.stock.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockSearchActivity extends AppCompatActivity {
    private static final int sAddBtnId = 100;

    private Profile mProfile;

    private RecyclerView mSuggestList;

    private EditText mSymbolInput;

    private Button mCleanBtn;

    private SuggestListAdapter mAdapter;

    private Call<List<StockQueryInfo>> mCall;

    private void refreshQueryStockSymbols() {
        Call<List<StockQueryInfo>> call = NetworkUtil.service.queryStockSymbol(AuthManager.getAccessToken(),mSymbolInput.getText().toString().toUpperCase());
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
        }
        mCall = call;

        call.enqueue(new Callback<List<StockQueryInfo>>() {
            @Override
            public void onResponse(Call<List<StockQueryInfo>> call, Response<List<StockQueryInfo>> response) {
                List<StockQueryInfo> resp = response.body();
                mAdapter.resetDataSource(resp);
            }

            @Override
            public void onFailure(Call<List<StockQueryInfo>> call, Throwable t) {
                System.out.println(t);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_search);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mSuggestList = (RecyclerView) findViewById(R.id.rv_stock_search_result);
        mSymbolInput = (EditText) findViewById(R.id.et_search_stock);
        mCleanBtn = (Button) findViewById(R.id.bt_clean_search_text);

        mCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSymbolInput.setText("");
            }
        });

        mSymbolInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    refreshQueryStockSymbols();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mSuggestList.setLayoutManager(manager);

        mAdapter = new SuggestListAdapter(new ArrayList<StockQueryInfo>(), new MyListener() {
            @Override
            public void clicked(StockQueryInfo info) {
                mSymbolInput.setText(info.symbol);
            }
        });
        mSuggestList.setAdapter(mAdapter);

        String profileStr = getIntent().getStringExtra("profile");
        mProfile = (Profile)Utility.ungsonObject(profileStr, Profile.class);

    }

    private interface MyListener {
        void clicked(StockQueryInfo info);
    }

    private class SuggestListAdapter extends RecyclerView.Adapter<SuggestListAdapter.SuggestViewHolder> {
        private List<StockQueryInfo> symbols;

        private MyListener listener;


        public SuggestListAdapter(List<StockQueryInfo> symbols, MyListener listener) {
            this.symbols = symbols;
            this.listener = listener;
        }

        public void resetDataSource(List<StockQueryInfo> symbols) {
            this.symbols = symbols;
            notifyDataSetChanged();
        }

        @Override
        public SuggestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.stock_suggest_list_item, parent, false);
            SuggestViewHolder holder = new SuggestViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(SuggestViewHolder holder, int position) {
            holder.bindItem(symbols.get(position));
        }

        @Override
        public int getItemCount() {
            return symbols.size();
        }

        class SuggestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView mTextView1;
            TextView mTextView2;

            public SuggestViewHolder(View itemView) {
                super(itemView);

                mTextView1 = itemView.findViewById(R.id.tv_symbol_suggest_symbol_name);
                mTextView2 = itemView.findViewById(R.id.tv_symbol_suggest_full_name);


                itemView.setOnClickListener(this);
            }

            public void bindItem(StockQueryInfo stock) {
                mTextView1.setText(stock.symbol);
                mTextView2.setText(stock.companyName);
            }

            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                StockQueryInfo info = symbols.get(pos);
                listener.clicked(info);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == sAddBtnId) {
//            Intent intent = new Intent(getString(R.string.broadcast_add_new_symbol));
//            intent.putExtra("new_symbol", this.mSymbolInput.getText().toString());
//            sendBroadcast(intent);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, sAddBtnId, 0, "add");

        return true;
    }
}
