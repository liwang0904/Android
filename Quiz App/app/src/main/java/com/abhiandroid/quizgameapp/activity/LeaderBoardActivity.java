package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.databinding.ActivityLeaderBoardBinding;
import com.abhiandroid.quizgameapp.fragments.LoginDialogfragment;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.interfaces.OnLoginCallbackListener;
import com.abhiandroid.quizgameapp.quiz_adapters.ViewPageradapter;
import com.abhiandroid.quizgameapp.utils.CommonUtils;

/**
 * Created by AbhiAndroid.com
 */

public class LeaderBoardActivity extends AppCompatActivity implements InternetRefreshCallback {
    private Context mContext;
    public static Activity mActivity;
    private ActivityLeaderBoardBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_leader_board);

        /**
         * Get Activity and Context class instances
         */
        mContext=getApplicationContext();
        mActivity=this;
        //Initiates Tabs
        /**
         * Check wether user logged-in or not.If user os logout then opened popup to login the user.
         */
        if(CommonUtils.getInstance().isUserLoggedIn(mContext)) {
            if(CommonUtils.getInstance().isNetworkConnected(mContext)) {
                mBinding.internetErrorPanel.setVisibility(View.GONE);
                initViewPager();
            }
            else{
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(LeaderBoardActivity.this);
            }


        }else{
            LoginDialogfragment newFragment =LoginDialogfragment.getInstance(mContext,mActivity, new OnLoginCallbackListener() {
                @Override
                public void OnCancel() {
                    finish();
                }

                @Override
                public void onSuccess() {
                    initViewPager();
                }
            });
            Bundle bundle=new Bundle();
            bundle.putString("message",getResources().getString(R.string.login_mandotry_leaderboard));
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }

public void goBack(View view){
        finish();
}
    private void initViewPager(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPageradapter adapter = new ViewPageradapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onInternetrefresh() {
        mBinding.internetErrorPanel.setVisibility(View.GONE);
        initViewPager();
    }
}

/**
 * Created by AbhiAndroid.com
 */