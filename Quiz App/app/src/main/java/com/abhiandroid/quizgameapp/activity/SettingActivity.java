package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.databinding.ActivitySettingBinding;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;

/**
 * Created by AbhiAndroid.com
 */

public class SettingActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    private ActivitySettingBinding mBinding;
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        mPref=SharedPreferences.getInstance(mContext);
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init_Notificationpanel();
        init_Vibrationpanel();
    }

    /**
     * Initialize Notification panel
     */
    private void init_Notificationpanel() {
        //Check is notification enabled or disable
        if(mPref.isNotificationEnabled())
            mBinding.scNotification.setChecked(true);
        else
            mBinding.scNotification.setChecked(false);


        mBinding.scNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.setnotification(isChecked);
            }
        });
    }
    /**
     * Initialize Vibration panel
     */
    private void init_Vibrationpanel() {
        //Check is notification enabled or disable
        if(mPref.isVibrationEnabled())
            mBinding.scVibration.setChecked(true);
        else
            mBinding.scVibration.setChecked(false);


        mBinding.scVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.setVibration(isChecked);
            }
        });
    }
}

/**
 * Created by AbhiAndroid.com
 */