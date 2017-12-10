package com.naughtypiggy.android.stock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.ApiUserResp;
import com.naughtypiggy.android.stock.network.model.Profile;
import com.naughtypiggy.android.stock.uis.AddProfileDialog;
import com.naughtypiggy.android.stock.utility.Utility;

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



public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddProfileDialog.AddProfileListener {
    Button mButton;

    private List<Profile> mProfiles;
    private RecyclerView mProfileListView;

    private final int mLoginId = 1;
    private final int mLogoutId = 2;

    private final String TAG = "Main Activity";


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
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                Profile profileStock = mProfiles.get(clikedItemIndex);
                                String profileString = Utility.gsonObject(profileStock);
                                intent.putExtra(getString(R.string.one_profile_key), profileString);
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
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        if (AuthManager.isAlreadyLoggedIn()) {
            menu.add(0, mLogoutId, 0, "Logout");
        } else {
            menu.add(0, mLoginId, 0, "Login");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == mLoginId) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == mLogoutId) {
            AuthManager.logout();
            invalidateOptionsMenu();
            return true;
        }

        if (item.getItemId() == R.id.menu_main_activity_add) {
            Log.d(TAG, "onOptionsItemSelected: add");

            FragmentManager manager = getFragmentManager();
            AddProfileDialog dialog = new AddProfileDialog();
            dialog.show(manager, "TEST");
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

    @Override
    public String onDialogPositiveSelected(String profileName) {
        Log.d(TAG, "onDialogPositiveSelected: add profile with name " + profileName);

        Call<ApiUserResp> call =  NetworkUtil.service.createNewProfile(AuthManager.getAccessToken(), profileName);
        call.enqueue(new Callback<ApiUserResp>() {
            @Override
            public void onResponse(Call<ApiUserResp> call, Response<ApiUserResp> response) {
                ApiUserResp resp = response.body();
                if (!resp.hasError) {
                    Toast.makeText(MainActivity.this, "succeed", Toast.LENGTH_SHORT).show();
                    MainActivity.this.refreshProfiles();
                } else {
                    Toast.makeText(MainActivity.this, resp.errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiUserResp> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return null;
    }
}
