package com.abhiandroid.quizgameapp.activity;

/**
 * Created by AbhiAndroid.com
 */

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;

import java.security.MessageDigest;

public class InitialOpenActivity extends ParentActicity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_open);
        /**
         * @Alert ParentActivity is a user_defined activity.
         * @Call ParentActivity's method to share intent with other activities.
         * @Params current class reference, Destination class address & thread waiting timeperiod.
         */
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                AppLog.getInstance().printLog(getApplicationContext(),"KeyHash::::"+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {

        }
        AppLog.getInstance().printLog(getApplicationContext(),CommonUtils.getInstance().isUserLoggedIn(getApplicationContext())+"");
        if(CommonUtils.getInstance().isUserLoggedIn(getApplicationContext()))
        super.startSplash(this,CategoryActivity.class,1500);
        else
            super.startSplash(this,Home_Activity.class,1500);
    }
}

/**
 * Created by AbhiAndroid.com
 */