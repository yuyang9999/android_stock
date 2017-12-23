package com.naughtypiggy.android.stock;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.Profile;
import com.naughtypiggy.android.stock.uis.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileScreenSliderActivity extends AppCompatActivity {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<Profile> mProfiles = new ArrayList<>();

    private void refreshProfiles() {
        Call<ApiResp.ApiProfileResp> call = NetworkUtil.service.getProfiles(AuthManager.getAccessToken());
        call.enqueue(new Callback<ApiResp.ApiProfileResp>() {
            @Override
            public void onResponse(Call<ApiResp.ApiProfileResp> call, Response<ApiResp.ApiProfileResp> response) {
                ApiResp.ApiProfileResp resp = response.body();
                if (resp != null) {
                    mProfiles = resp.response;
                    mPagerAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_profile_screen_slider);

        mPager = (ViewPager) findViewById(R.id.profile_pager);
        mPagerAdapter = new ProfileScreenSliderpagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProfiles();
    }

    private class ProfileScreenSliderpagerAdapter extends FragmentStatePagerAdapter {
        public ProfileScreenSliderpagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Profile p = mProfiles.get(position);

            ProfileFragment fragment = ProfileFragment.createFragment(p);
            return fragment;
        }

        @Override
        public int getCount() {
            return mProfiles.size();
        }
    }
}
