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
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Internet_error_panel extends LinearLayout {
    LayoutInflater mLayoutInflater;
    public Context mContext;
        View view;
    public Internet_error_panel(Context context) {
        super(context);

        AppLog.getInstance().printLog(context,"first");
    }

    public Internet_error_panel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext=getApplicationContext();
        mLayoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=mLayoutInflater.inflate(R.layout.internet_error_layout,this);
        //getParent().add(view);

    }
    public Internet_error_panel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        AppLog.getInstance().printLog(context,"third");
    }
    public void setOnRefreshListener(final InternetRefreshCallback internetRefreshCallback){
        TextView    try_again=view.findViewById(R.id.try_again);
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetRefreshCallback.onInternetrefresh();
            }
        });
    }

}
