package com.naughtypiggy.android.stock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.Profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private List<Profile> mProfiles;
    private ListItemClickListener mClickListener;

    public ProfileAdapter(List<Profile> profiles, ListItemClickListener listener) {
        mClickListener = listener;
        mProfiles = profiles;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clikedItemIndex);
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdOfListItem = R.layout.profile_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdOfListItem, parent, false);
        ProfileViewHolder viewHolder = new ProfileViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mProfileTextView;

        public ProfileViewHolder(View itemView) {
            super(itemView);

            mProfileTextView = (TextView) itemView.findViewById(R.id.tv_item_profile);
            itemView.setOnClickListener(this);
        }

        void  bind(int listIndex) {
            mProfileTextView.setText(mProfiles.get(listIndex).getPname());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickListener.onListItemClick(clickedPosition);
        }
    }
}



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mButton;

    private List<Profile> mProfiles;
    private RecyclerView mProfileListView;


    private void refreshProfiles() {
        Call<ApiResp.ApiProfileResp> req = NetworkUtil.service.getProfiles(AuthManager.getAccessToken());
        req.enqueue(new Callback<ApiResp.ApiProfileResp>() {
            @Override
            public void onResponse(Call<ApiResp.ApiProfileResp> call, Response<ApiResp.ApiProfileResp> response) {
                if (response.isSuccessful()) {
                    ApiResp.ApiProfileResp resp = response.body();
                    if (!resp.hasError) {
                        mProfiles = resp.response;

                        ProfileAdapter adapter = new ProfileAdapter(mProfiles, new ProfileAdapter.ListItemClickListener() {
                            @Override
                            public void onListItemClick(int clikedItemIndex) {
                                Intent intent = new Intent(MainActivity.this, ProfileStockActivity.class);

                                startActivity(intent);
                            }
                        });
                        mProfileListView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResp.ApiProfileResp> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mButton = (Button) findViewById(R.id.testButton);
//        mButton.setOnClickListener(this);

        mProfileListView = (RecyclerView) findViewById(R.id.rv_profileList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mProfileListView.setLayoutManager(layoutManager);
        mProfileListView.setHasFixedSize(true);
        setTitle("Profiles");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProfiles();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AuthManager.isAlreadyLoggedIn()) {
            getMenuInflater().inflate(R.menu.main_activity_logout, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_activity_login, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            AuthManager.logout();
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.testButton) {
//            Intent intent = new Intent(this, RegisterActivity.class);
//            startActivity(intent);
//        }
    }
}
