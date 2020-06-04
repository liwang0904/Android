package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityMainBinding;
import com.abhiandroid.quizgameapp.model.User;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AbhiAndroid
 */

public class Home_Activity extends ParentActicity
{
    ActivityMainBinding mBinding;
    private Context mContext;
    private Activity mActivity;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private APIInterface apiInterface;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=101;
    private final int GOOGLE_LOGIN_CANECLED=13;
    private final int GOOGLE_LOGIN_FAILURE=7;
   // int fb_login_id=mBinding.
  // private RippleDrawable rippleDrawable;
   Set<String> deniedPermissions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        init_GLogin();

        setRippleEffectOnViews();

    }

    private void setRippleEffectOnViews() {

       CommonUtils mUtils= CommonUtils.getInstance();
       mUtils.setRippleEffect(mBinding.tvFbLogin);
       mUtils.setRippleEffect(mBinding.tvGoogleLogin);

    }

    private void init_GLogin() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
       // updateUI(account);

       /* SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);*/

        findViewById(R.id.tv_google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    public void fbLogin(View view)
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL,PUBLIC_PROFILE));
       // LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        AppLog.getInstance().printLog(mContext,"Login success");
                        try{
                        deniedPermissions=  AccessToken.getCurrentAccessToken().getDeclinedPermissions();
                        if (deniedPermissions.contains(EMAIL)) {
                            showEmailMandatoryPopop();
                        }}catch (Exception e){

                        }finally {
                            deniedPermissions=null;
                        }




                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            String email = object.optString("email");
                                            String name = object.getString("name");
                                            //  String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                            String id = object.getString("id");
                                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=large";
                                            if(!(email==null||email.isEmpty()))
                                             makeLoginReq(name,email,image_url,id, GlobalConstant.getInstance().LOGIN_TYPE_FACEBOOK);



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel()
                    {
                        // App code

                       try{
                        deniedPermissions=  AccessToken.getCurrentAccessToken().getDeclinedPermissions();
                        if (deniedPermissions.contains(EMAIL)) {
                            AppLog.getInstance().printToast(mContext,"Without your email we're not able to create your account.");
                            deniedPermissions=null;
                        }else{
                            AppLog.getInstance().printToast(mContext,"Login Cancelled");

                        }}catch (Exception e){
                           AppLog.getInstance().printToast(mContext,"Login Cancelled");
                           AppLog.getInstance().printLog(mContext,"login cancelled::");
                       }


                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        // App code
                        AppLog.getInstance().printLog(mContext,"login error::");
                        if(exception.toString().equalsIgnoreCase("User logged in as different Facebook user."))
                            AppLog.getInstance().printToast(mContext,"Something went wrong with facebook login.Please try google login.");
                        else
                            AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_error));

                    }
                });
    }


    private void makeLoginReq(String name,String email,String profile_img,String social_id,String login_type) {
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        /**
         Create new user
         **/
        User user=new User();
        User.Credentials credentials=user.new Credentials();
        credentials.email = email;
        credentials.name = name;
        credentials.logintype = login_type;
        credentials.social_id = social_id;
        credentials.profile_pic = profile_img;

        Call<User> call1 = apiInterface.createUser(credentials);
        call1.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                pDialog.dismiss();
                User user = response.body();

                SharedPreferences.getInstance(mContext).saveLoggedInUser(
                        user.data.name,
                        user.data.email,
                        user.data.profile_pic,
                        user.data.logintype,
                        user.data.social_id,
                        user.data.student_id
                );

                Home_Activity.super.sendIntent(CategoryActivity.class,null);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mContext,"failed response");
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
            }
        });

    }
    public void showEmailMandatoryPopop(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.email_mandatory)
                .setPositiveButton("Ok! Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        dialog.dismiss();

                    }
                });

        builder.create().show();
        // Create the AlertDialog object and return it
        //return builder.create();
    }
    public void takeQuiz(View view){
       super.sendIntent(CategoryActivity.class,null);
       finish();
        // AppLog.getInstance().printToast(mContext,"Work on progress");
    }

    public void g_Login(View view){
        AppLog.getInstance().printToast(mContext,"Work on progress");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInResult result  = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(task,result);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask, GoogleSignInResult result) {
        try {
            if (result.isSuccess()) {
                AppLog.getInstance().printLog(mContext,"google login success");
                GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

                // Signed in successfully, show authenticated UI.

                if (acct != null) {
                    String personName = acct.getDisplayName();
                    String personGivenName = acct.getGivenName();
                    String personFamilyName = acct.getFamilyName();
                    String personEmail = acct.getEmail();
                    String personId = acct.getId();
                    Uri personPhoto = acct.getPhotoUrl();

                    makeLoginReq(personName, personEmail, personPhoto == null ? "" : personPhoto.toString(), personId, GlobalConstant.getInstance().LOGIN_TYPE_GOOGLE);
                    mGoogleSignInClient.signOut();
                }
            }else{

                Status status = result.getStatus();
                int statusCode = status.getStatusCode();
                AppLog.getInstance().printLog(mContext,"google login failed");

                if (statusCode == GOOGLE_LOGIN_CANECLED) {
                    AppLog.getInstance().printToast(mContext,"Google Login cancelled");
                }
                else if (statusCode == GOOGLE_LOGIN_FAILURE) {
                    AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_error));
                }

            }
         //   updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            int statusCode = e.getStatusCode();

            //updateUI(null);
        }
    }



}

/**
 * Created by AbhiAndroid
 */