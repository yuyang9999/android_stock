package com.naughtypiggy.android.stock;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.baoyz.actionsheet.ActionSheet;
import com.naughtypiggy.android.stock.network.AuthManager;
import com.naughtypiggy.android.stock.network.NetworkUtil;
import com.naughtypiggy.android.stock.network.model.ApiResp;
import com.naughtypiggy.android.stock.network.model.Profile;
import com.naughtypiggy.android.stock.uis.AddProfileDialog;
import com.naughtypiggy.android.stock.uis.ProfileFragment;
import com.naughtypiggy.android.stock.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileScreenSliderActivity extends AppCompatActivity {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<Profile> mProfiles = new ArrayList<>();

    private static final int ADD_ITEM_ID = 100;
    private static final int MORE_ITEM_ID = 101;

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            View leftMenu = getLayoutInflater().inflate(R.layout.profile_slider_action_bar, null);
            Button btn = (Button) leftMenu.findViewById(R.id.btn_login);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileScreenSliderActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
            actionBar.setCustomView(leftMenu);
            ;
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProfiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, ADD_ITEM_ID, Menu.NONE, "add symbol");
        menu.add(Menu.NONE, MORE_ITEM_ID, Menu.NONE, "more");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == ADD_ITEM_ID) {
            Intent intent = new Intent(this, StockSearchActivity.class);

            int pos = mPager.getCurrentItem();
            if (pos < mProfiles.size()) {
                Profile profile = mProfiles.get(mPager.getCurrentItem());
                String profileStr = Utility.gsonObject(profile);
                intent.putExtra("profile", profileStr);
                startActivity(intent);
            }

            return true;
        } else if (item.getItemId() == MORE_ITEM_ID) {
            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles("Create new profile", "Delete profile")
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            if (index == 0) {
                                android.app.FragmentManager manager = getFragmentManager();
                                AddProfileDialog dialog = new AddProfileDialog();
                                dialog.show(manager, "add_profile_dialog");
                            } else if (index == 1) {
                                //delete profile
                                //todo delete the profile
                            }
                        }
                    }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            if (position < mProfiles.size()) {
                String profileName = mProfiles.get(position).getPname();
                setTitle(profileName);
            }
        }

        @Override
        public int getCount() {
            return mProfiles.size();
        }
    }
}
