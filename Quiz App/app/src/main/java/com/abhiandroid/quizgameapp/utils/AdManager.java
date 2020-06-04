package com.abhiandroid.quizgameapp.utils;

import android.content.Context;

import com.abhiandroid.quizgameapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Developed by AbhiAndroid.com
 */

public class AdManager {
    // Static fields are shared between all instances.
    static InterstitialAd interstitialAd;
     Context mContext;
    public AdManager(Context context) {

        mContext=context;
        createAd();
    }

    public void createAd() {
        // Create an ad.
        interstitialAd = new InterstitialAd(mContext);
        interstitialAd.setAdUnitId(mContext.getResources().getString(R.string.interstitial_unit_id));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6B5FDAFD74F5221BA81E852BF4076708").build();

        // Load the interstitial ad.
        interstitialAd.loadAd(adRequest);
    }

    public InterstitialAd getAd() {
        return interstitialAd;
    }
}
