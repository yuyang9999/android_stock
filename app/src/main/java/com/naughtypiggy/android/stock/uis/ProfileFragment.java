package com.naughtypiggy.android.stock.uis;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naughtypiggy.android.stock.R;
import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.Profile;
import com.naughtypiggy.android.stock.network.model.ProfileStock;
import com.naughtypiggy.android.stock.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yangyu on 23/12/17.
 */
public class ProfileFragment extends Fragment {
    public static final String ARG_PROFILE_NAME = "profile_name";


    private Profile mProfile;
    private List<ProfileStock> mStocks = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;

    public static ProfileFragment createFragment(Profile profile) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        String profileGson = Utility.gsonObject(profile);
        args.putString(ARG_PROFILE_NAME, profileGson);
        fragment.setArguments(args);
        return fragment;
    }

    private void refreshStocks() {
        Call<ApiResp.ApiProfileSymbolResp> call = NetworkUtil.service.getProfileSymbols(AuthManager.getAccessToken(), mProfile.getPname());
        call.enqueue(new Callback<ApiResp.ApiProfileSymbolResp>() {
            @Override
            public void onResponse(Call<ApiResp.ApiProfileSymbolResp> call, Response<ApiResp.ApiProfileSymbolResp> response) {
                ApiResp.ApiProfileSymbolResp resp = response.body();
                if (resp != null) {
                    mStocks = resp.response;
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResp.ApiProfileSymbolResp> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.mStocks.size() == 0) {
            refreshStocks();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String profileStr = getArguments().getString(ARG_PROFILE_NAME);
        mProfile = (Profile)Utility.ungsonObject(profileStr, Profile.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.profile_fragment, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_profile_fragment);

        LinearLayoutManager manager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(manager);

        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return rootView;
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.profile_recycler_view_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(mStocks.get(position));
        }

        @Override
        public int getItemCount() {
            return mStocks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.tv_profile_recycler_stock);
            }

            public void bind(ProfileStock stock) {
                mTextView.setText(stock.getSname());
            }
        }
    }
}
