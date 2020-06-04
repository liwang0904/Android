package com.abhiandroid.quizgameapp.appcontroller;

import android.app.Application;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by AbhiAndroid.com
 */

public class ApplicationController extends Application {
    public static InterstitialAd mInterstitialAd;
    private static ApplicationController mAppController;
    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.getInstance().printLog(getApplicationContext(),"Application called");
        MobileAds.initialize(this,
                getResources().getString(R.string.addmob_app_id));

        mInterstitialAd = new InterstitialAd(this);
        //  mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_unit_id));
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        AdRequest request = new AdRequest.Builder()
                .addTestDevice("6B5FDAFD74F5221BA81E852BF4076708")  // An example device ID
                .build();
        mInterstitialAd.loadAd(request);
        mAppController=this;
    }
    public  static ApplicationController getInstance(){
        return mAppController;
    }
}
