package com.naughtypiggy.android.stock;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.naughtypiggy.android.stock.broadcast.NewSymbolAddReceiver;
import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.Profile;
import com.naughtypiggy.android.stock.network.model.ProfileStock;
import com.naughtypiggy.android.stock.network.model.StockSymbol;
import com.naughtypiggy.android.stock.uis.AddProfileStockDialog;
import com.naughtypiggy.android.stock.utility.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements AddProfileStockDialog.AddProfileStockListener {

    static private final String TAG = "ProfileActivity";
    static private final int ITEM_ID_ADD = 10;

    private Profile mProfile;
    private List<ProfileStock> mStocks;

    private RecyclerView mStockListView;
    NewSymbolAddReceiver mReceiver;


    private void deleteStock(final int position) {
        ProfileStock stock = mStocks.get(position);
        Call<ApiResp.ApiBooleanResp> call = NetworkUtil.service.deleteProfileStock(AuthManager.getAccessToken(), mProfile.getPname(), stock.getSid());
        call.enqueue(new Callback<ApiResp.ApiBooleanResp>() {
            @Override
            public void onResponse(Call<ApiResp.ApiBooleanResp> call, Response<ApiResp.ApiBooleanResp> response) {
                ApiResp.ApiBooleanResp resp = response.body();
                if (resp.hasError) {
                    String errorMsg = resp.errorMsg;
                    Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_SHORT);
                } else {
                    //delete the data
                    mStocks.remove(position);
                    StocksAdapter adapter = (StocksAdapter) mStockListView.getAdapter();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResp.ApiBooleanResp> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        listener.clicked(pos);
                    }
                });
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

        mReceiver = new NewSymbolAddReceiver(new NewSymbolAddReceiver.ReceiveInterface() {
            @Override
            public void receiveIntent(Intent intent) {


                System.out.println("receive intent" + intent);
            }
        });

        registerReceiver(mReceiver, new IntentFilter(getString(R.string.broadcast_add_new_symbol)));
        setupRecyclerViewGestures();

        setTitle("Stocks");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ITEM_ID_ADD, 0, "ADD");
        return true;
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
            return true;
        }

        if (item.getItemId() == ITEM_ID_ADD) {
            /*
            FragmentManager manager = getFragmentManager();

            AddProfileStockDialog dialog = new AddProfileStockDialog();
            dialog.show(manager, "add_stock_dialog");
            */
            Intent intent = new Intent(ProfileActivity.this, StockSearchActivity.class);
            String profileStr = Utility.gsonObject(mProfile);
            intent.putExtra("profile", profileStr);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public String onDialogPositiveSelected(String sname, int shares, float price, String boughtDate) {
        Call<ApiResp.ApiBooleanResp> call = NetworkUtil.service.addProfileSymbol(AuthManager.getAccessToken(),
                mProfile.getPname(), sname, shares, price, boughtDate);
        call.enqueue(new Callback<ApiResp.ApiBooleanResp>() {
            @Override
            public void onResponse(Call<ApiResp.ApiBooleanResp> call, Response<ApiResp.ApiBooleanResp> response) {
                ApiResp.ApiBooleanResp resp = response.body();
                if (!resp.hasError) {
                    refreshProfileStocks();
                } else {
                    Log.e(TAG, "onResponse: " + resp.errorMsg, null);
                }
            }

            @Override
            public void onFailure(Call<ApiResp.ApiBooleanResp> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });


        return null;
    }


    private void setupRecyclerViewGestures() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = 16;
                initiated = true;

            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                deleteStock(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    //not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                //draw the background
                background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                //draw the x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicHeight();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xmarkBottom = xMarkTop + intrinsicHeight;

                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xmarkBottom);
                xMark.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(mStockListView);
    }


}
