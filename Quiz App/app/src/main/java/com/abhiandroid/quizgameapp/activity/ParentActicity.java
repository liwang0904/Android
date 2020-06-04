package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.appcontroller.ApplicationController;
import com.abhiandroid.quizgameapp.fragments.LoginDialogfragment;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.utils.AdManager;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by AbhiAndroid.com
 */

public class ParentActicity extends Activity {
    private InterstitialAd mInterstitialAd;
    AdManager adManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         adManager = new AdManager(getApplicationContext());
       // adManager.createAd();
    }
    public void showInterstitialAds(){
        InterstitialAd ad = adManager.getAd();
        if (ad.isLoaded()) {
            ad.show();
        } else {
        AppLog.getInstance().printLog(getApplicationContext(), "The interstitial wasn't loaded yet.");
    }


    }
   public void sendIntent(Class<?> destination_address,Bundle bundle){
       Intent intent=new Intent(this,destination_address);
        intent.putExtra("bundle",bundle);
       startActivity(intent);
       overridePendingTransition(R.anim.enter, R.anim.exit);
   }
    public void startSplash(final Activity baseAddress, final Class destination_address, final int time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    Intent intent=new Intent(baseAddress,destination_address);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
            showInterstitialAds();
    }

}

/**
 * Created by AbhiAndroid.com
 */