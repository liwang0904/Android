package com.abhiandroid.quizgameapp.activity;

/**
 * Created by AbhiAndroid.com
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityProfileBinding;
import com.abhiandroid.quizgameapp.fragments.LoginDialogfragment;
import com.abhiandroid.quizgameapp.interfaces.OnLoginCallbackListener;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.google.android.gms.common.api.ResultCallback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends ParentActicity {
    ActivityProfileBinding mBinding;
    private Context mContext;
    private Activity mActivity;
    private APIInterface apiInterface;
    private LoginDialogfragment newFragment;
    private SharedPreferences mPref;
    private GlobalConstant mGConstant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        /**
         * initialize back button
         */
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Get logged-in user prefernces data
        mPref=SharedPreferences.getInstance(mContext);
         mGConstant=GlobalConstant.getInstance();

         //Logout  User

        /**
         * Check wether user Login or not? if yes then initialize all views otherwise login the user.
         */
       // if(CommonUtils.getInstance().isUserLoggedIn(mContext)) {
            initviews();
        //}else
          //  login();



    }

    private void initviews() {
        HashMap<String,String> user_data= mPref.getLoggedUserData();
          //  mBinding.tvUsernamename.setText(mGConstant.NAME);
            mBinding.username.setText(user_data.get(mGConstant.NAME));
            mBinding.email.setText(user_data.get(mGConstant.EMAIL));
        Picasso.with(mContext).load(user_data.get(mGConstant.IMAGE)).fit().placeholder(R.drawable.login_required).into(mBinding.ivProfileImg);
    }


    private void login() {
        /**
         * Login fragment dialog used to login and save data in prefernces then trigger a callback method.
         */
         newFragment =LoginDialogfragment.getInstance(mContext,mActivity, new OnLoginCallbackListener() {
            @Override
            public void OnCancel() {
                finish();
            }

            @Override
            public void onSuccess() {

                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.logged_in_successfully));
                initviews();
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("message",getResources().getString(R.string.profile_login_message));
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Logout User
     * @param view
     */
    public void logout(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.want_to_logout)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                         SharedPreferences.getInstance(mContext).logoutUser();
                         ProfileActivity.super.sendIntent(Home_Activity.class,null);
                         CommonUtils.getInstance().clearStack();
                            finish();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        // Create the AlertDialog object and return it
        //return builder.create();
    }


}

/**
 * Created by AbhiAndroid.com
 */