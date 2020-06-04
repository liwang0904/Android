package com.abhiandroid.quizgameapp.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abhiandroid.quizgameapp.R;

/**
 * Developed by AbhiAndroid.com
 */

public class AppLog {
    /**
     *
     * @return current class instance
     */
    public static AppLog getInstance(){
        return  new AppLog();
    }

    /**
     *
     * @param context
     * @param message
     */
    public void printToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param context
     * @param message
     */
    public void printLog(Context context,String message){
       if(message!=null)
        Log.d("START",message);
    }

    public void printSnackbar(Context context,String message){
       /* LayoutInflater mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=mInflater.inflate(R.layout.snackbar_layout,null);
        TextView   tv_message=view.findViewById(R.id.message);
        tv_message.setText(message+"");
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();*/
    }
}
