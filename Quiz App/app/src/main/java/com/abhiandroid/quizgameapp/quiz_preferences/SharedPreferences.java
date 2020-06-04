package com.abhiandroid.quizgameapp.quiz_preferences;

import android.content.Context;

import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.utils.AppLog;

import java.util.HashMap;

/**
 * Developed by AbhiAndroid.com
 */

public class SharedPreferences {
   private static SharedPreferences mPref;
   private static Context mContext;

    /**
     * GET current class instance.
     * @param context
     * @return
     */
    public static SharedPreferences getInstance(Context context){
        mPref=new SharedPreferences();
        mContext=context;
        return mPref;
    }

    /**
     * Save Logged-In User data.
     * @param name
     * @param email
     * @param image_url
     * @param login_type
     * @param social_id
     */
    public void saveLoggedInUser(String name,String email,String image_url,String login_type,String social_id,String student_id){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.USER_TABLE,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor=sp.edit();
        editor.putString(mGConstant.NAME,name);
        editor.putString(mGConstant.EMAIL,email);
        editor.putString(mGConstant.IMAGE,image_url);
        editor.putString(mGConstant.LOGIN_TYPE,login_type);
        editor.putString(mGConstant.SOCIAL_ID,social_id);
        editor.putString(mGConstant.STUDENT_ID,student_id);
        editor.commit();
    }

    /**
     * Get Logged-In User data by passing keys
     * @param key
     * @return
     */
    public String  getLoggedUserData(String key){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.USER_TABLE,
                Context.MODE_PRIVATE);
        return sp.getString(key,null);
    }
    /**
     * Get Logged-In User data all values
     * @return
     */
    public HashMap<String,String>  getLoggedUserData()throws NullPointerException{
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.USER_TABLE,
                Context.MODE_PRIVATE);
        HashMap<String,String> user_data=new HashMap<>();

        user_data.put(mGConstant.NAME,sp.getString(mGConstant.NAME,null));
        user_data.put(mGConstant.EMAIL,sp.getString(mGConstant.EMAIL,null));
        user_data.put(mGConstant.IMAGE,sp.getString(mGConstant.IMAGE,null));
        user_data.put(mGConstant.LOGIN_TYPE,sp.getString(mGConstant.LOGIN_TYPE,null));
        user_data.put(mGConstant.SOCIAL_ID,sp.getString(mGConstant.SOCIAL_ID,null));
        user_data.put(mGConstant.STUDENT_ID,sp.getString(mGConstant.STUDENT_ID,null));
        return user_data;
    }

    /**
     * Logout user and clear prefs
     * @return
     */
    public boolean logoutUser(){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.USER_TABLE,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor=sp.edit();
        editor.clear().commit();
        return true;
    }

    /**
     * check back press count for interstitial Ads
     * @return
     */
    public boolean checkTimeForInterstitialsAds(){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.TIME_TABLE,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor=sp.edit();
        int counter=sp.getInt(mGConstant.TIMES,0);
        AppLog.getInstance().printLog(mContext,"counter::::"+counter );
        if(counter==2 ){
            editor.clear().commit();
            return true;
        }else{
            editor.putInt(mGConstant.TIMES,++counter);
            editor.commit();
            return false;
        }

    }



    /**
     * Save FCM Token while app install automatically. Pass this token at login api to update the data at backend.
     * @param token
     */
    public void saveFCMToken(String token){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.FCM_TABLE,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor=sp.edit();
        editor.putString(mGConstant.TOKEN,token);

        editor.commit();
    }

    /**
     * Fetch FCM Token from SharedPref..
     */
    public String getFCMToken(){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.FCM_TABLE,
                Context.MODE_PRIVATE);

        return  sp.getString(mGConstant.TOKEN,null);
    }

    /**
     * Make Notification Enable/Disable using this function
     */
    public void setnotification(boolean isEnable){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.FCM_TABLE,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(mGConstant.NOTIFICATION,isEnable).commit();
    }
    /**
     * Check Notification Enable/Disable?
     */
    public boolean isNotificationEnabled(){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.FCM_TABLE,
                Context.MODE_PRIVATE);

        return sp.getBoolean(mGConstant.NOTIFICATION,true);
    }
    /**
     * Make Vibration Enable/Disable using this function
     */
    public void setVibration(boolean isEnable){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.FCM_TABLE,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(mGConstant.VIBRATION,isEnable).commit();
    }
    /**
     * Check Vibration Enable/Disable?
     */
    public boolean isVibrationEnabled(){
        GlobalConstant mGConstant=GlobalConstant.getInstance();
        android.content.SharedPreferences sp=mContext.getSharedPreferences(mGConstant.FCM_TABLE,
                Context.MODE_PRIVATE);

        return sp.getBoolean(mGConstant.VIBRATION,true);
    }
}


/**
 * Developed by AbhiAndroid.com
 */