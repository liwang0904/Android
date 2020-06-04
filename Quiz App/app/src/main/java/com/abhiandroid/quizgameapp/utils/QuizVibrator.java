package com.abhiandroid.quizgameapp.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

/**
 * Developed by AbhiAndroid.com
 */

public class QuizVibrator {
    private final int VIBRATOR_OREO_TIME=200;
    private static Context mContext;
    public static QuizVibrator getInstance(Context context){
        mContext=context;
        return new QuizVibrator();
    }

    public void vibrate(int sec){
        Vibrator v = (Vibrator)mContext. getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

             v.vibrate(VibrationEffect.createOneShot(VIBRATOR_OREO_TIME,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(sec);
        }
    }
}
