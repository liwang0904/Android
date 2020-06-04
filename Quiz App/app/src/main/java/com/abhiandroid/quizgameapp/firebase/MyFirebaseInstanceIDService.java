package com.abhiandroid.quizgameapp.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.PushNotification_Pojo;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Developed by AbhiAndroid.com
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    private APIInterface apiInterface;

    private Context mContext;


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        mContext=getApplicationContext();
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //FCM Token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Save FCM Token to SharedPref. to use further.
        com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences.getInstance(mContext).saveFCMToken(refreshedToken);
        AppLog.getInstance().printLog(getApplicationContext(), "Refreshed token: " + refreshedToken);

        //Update the FCM Token at backend.
        Call<PushNotification_Pojo> call1 = apiInterface.sendToken("",refreshedToken);
        AppLog.getInstance().printLog(mContext,"call method");
        call1.enqueue(new Callback<PushNotification_Pojo>() {
            @Override
            public void onResponse(Call<PushNotification_Pojo> call, Response<PushNotification_Pojo> response) {

                PushNotification_Pojo notification = response.body();
                AppLog.getInstance().printLog(mContext,"token added succesfully");
                AppLog.getInstance().printLog(mContext,"token added succesfully:::"+notification.message);

            }

            @Override
            public void onFailure(Call<PushNotification_Pojo> call, Throwable t) {

                call.cancel();
                AppLog.getInstance().printLog(mContext,"token not added");

            }
        });

    }
}


/**
 * Developed by AbhiAndroid.com
 */