package com.abhiandroid.quizgameapp.views;

/**
 * Developed by AbhiAndroid.com
 */

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyAdView extends LinearLayout {
    LayoutInflater mLayoutInflater;
    public Context mContext;
        View view;
    public MyAdView(Context context) {
        super(context);

        AppLog.getInstance().printLog(context,"second");
        mContext=getApplicationContext();
        mLayoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=mLayoutInflater.inflate(R.layout.layout_adview_,this);
        //getParent().add(view);
        init_Ads();
    }

    public MyAdView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        AppLog.getInstance().printLog(context,"second");
        mContext=getApplicationContext();
        mLayoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=mLayoutInflater.inflate(R.layout.layout_adview_,this);
        //getParent().add(view);
        init_Ads();
    }

    private void init_Ads() {
        mContext=getApplicationContext();
        MobileAds.initialize(mContext, getResources().getString(R.string.addmob_app_id));
        AdView mAdView = (AdView)view. findViewById(R.id.adView);

        String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        String deviceId = md5(android_id).toUpperCase();
        AppLog.getInstance().printLog(  mContext,"deviceId:::::"+deviceId);
//        mAdView.setAdSize(AdSize.BANNER);
        //        mAdView.setAdUnitId(getResources().getString(R.string.banner_unit_id));
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.loadAd(adRequest);
    }

    public MyAdView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        AppLog.getInstance().printLog(context,"second");
        mContext=getApplicationContext();
        mLayoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=mLayoutInflater.inflate(R.layout.layout_adview_,this);
        //getParent().add(view);
        init_Ads();
    }
    public  final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            AppLog.getInstance().printLog(  mContext,"exception:::::"+e);
        }
        return "";
    }
}

/**
 * Developed by AbhiAndroid.com
 */
