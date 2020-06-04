package com.abhiandroid.quizgameapp.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.activity.QuizActivity;
import com.abhiandroid.quizgameapp.activity.StartQuizActivity;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;


/**
 * Developed by AbhiAndroid.com
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    NotificationManager notificationManager;
    Context mContext;
    public static final String ANDROID_CHANNEL_ID = " com.abhiandroid.quizgameapp";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mContext=getApplicationContext();
        notificationManager =
                 (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannels();
            String title=remoteMessage.getData().get("title");
            String message=remoteMessage.getData().get("message");
            String image=remoteMessage.getData().get("image");
            String quiz_id=remoteMessage.getData().get("quiz_id");
            String subcat_id=remoteMessage.getData().get("subcat_id");
            int random_id=Integer.parseInt(remoteMessage.getData().get("random_id"));
            if(SharedPreferences.getInstance(mContext).isNotificationEnabled())
                notificationManager.notify(0 /* ID of notification */,  getAndroidChannelNotification(title,message,image,quiz_id,subcat_id).build());
        }else{
            if(SharedPreferences.getInstance(mContext).isNotificationEnabled())
                createNotification(remoteMessage);
        }


        /////////////////////////
    }


    private void createNotification(RemoteMessage remoteMessage) {
        /**
         * Get Data from FCM message
         */
        //String title=remoteMessage.getData().get("title");
        String title=remoteMessage.getData().get("title");
        String message=remoteMessage.getData().get("message");
        String image=remoteMessage.getData().get("image");
        String quiz_id=remoteMessage.getData().get("quiz_id");
        String subcat_id=remoteMessage.getData().get("subcat_id");
        int random_id=Integer.parseInt(remoteMessage.getData().get("random_id"));
        /**
         * Create Intent according to FCM data
         */


        Bitmap remote_picture = null;
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setSummaryText(message);
        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(image).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        notiStyle.bigPicture(remote_picture);


        Intent notificationIntent;
        Bundle bundle=new Bundle();
        if(CommonUtils.getInstance().isUserLoggedIn(mContext)){

            bundle.putString("quiz_id",quiz_id);
            AppLog.getInstance().printLog(mContext,"typeeeeeeee before::::::"+GlobalConstant.getInstance().TYPE_NOTIFICATION);
            AppLog.getInstance().printLog(mContext,"quiz_id before::::::"+quiz_id);
            bundle.putString("intent_from",  GlobalConstant.getInstance().TYPE_NOTIFICATION);
            notificationIntent = new Intent(mContext,StartQuizActivity.class).putExtra("bundle",bundle);
        }else{
            bundle.putString("subcat_id",subcat_id);
            bundle.putString("intent_from", GlobalConstant.getInstance().TYPE_NOTIFICATION);
            notificationIntent = new Intent(mContext,QuizActivity.class).putExtra("bundle",bundle);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,   notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
       /* NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ANDROID_CHANNEL_ID)
                .setSmallIcon(R.drawable.quiz_logo)  //a resource for your custom small icon
                .setContentTitle(title) //the "title" value you sent in your notification
                .setContentText(message) //ditto
                .setContentIntent(contentIntent)
                .setWhen(0)
                .setAutoCancel(true)  //dismisses the notification on click
                .setSound(defaultSoundUri)
                 .setChannelId(ANDROID_CHANNEL_ID);
*/
        Notification.Builder mBuilder = new Notification.Builder(
                mContext);
        Notification notification = mBuilder.setSmallIcon(R.drawable.quiz_logo).setTicker(title).setWhen(0)
                .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.quiz_logo)).getBitmap())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setPriority(Notification.PRIORITY_HIGH)
               // .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setStyle(new Notification.BigPictureStyle()
                        .bigPicture(remote_picture)
                        .setBigContentTitle(title))
                .setContentIntent(contentIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(message).build();
               // .setStyle(notiStyle).build();

            notificationManager.notify(0 /* ID of notification */, notification);

    }
    /*
     * Create a notification channel
     * */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {

        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        notificationManager.createNotificationChannel(androidChannel);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(String title, String message, String image, String quiz_id,String subcat_id) {
        Bitmap remote_picture = null;
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setSummaryText(message);
        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(image).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        notiStyle.bigPicture(remote_picture);

        Intent notificationIntent;
        Bundle bundle=new Bundle();
        if(CommonUtils.getInstance().isUserLoggedIn(mContext)){

            bundle.putString("quiz_id",quiz_id);
            AppLog.getInstance().printLog(mContext,"typeeeeeeee before::::::"+GlobalConstant.getInstance().TYPE_NOTIFICATION);
            AppLog.getInstance().printLog(mContext,"quiz_id before::::::"+quiz_id);
            bundle.putString("intent_from",  GlobalConstant.getInstance().TYPE_NOTIFICATION);
            notificationIntent = new Intent(mContext,StartQuizActivity.class).putExtra("bundle",bundle);
        }else{
            bundle.putString("subcat_id",subcat_id);
            bundle.putString("intent_from", GlobalConstant.getInstance().TYPE_NOTIFICATION);
            notificationIntent = new Intent(mContext,QuizActivity.class).putExtra("bundle",bundle);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,   notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.quiz_logo)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setStyle(new Notification.BigPictureStyle()
                        .bigPicture(remote_picture)
                        .setBigContentTitle(title))
                ;
    }
}


/**
 * Developed by AbhiAndroid.com
 */