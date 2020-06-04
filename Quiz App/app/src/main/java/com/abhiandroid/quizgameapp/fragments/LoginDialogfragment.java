package com.abhiandroid.quizgameapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.activity.QuizActivity;
import com.abhiandroid.quizgameapp.activity.StartQuizActivity;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.FragmentLoginDialogBinding;
import com.abhiandroid.quizgameapp.interfaces.OnLoginCallbackListener;
import com.abhiandroid.quizgameapp.model.User;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Developed by AbhiAndroid.com
 */

public class LoginDialogfragment extends DialogFragment {

    private View view;
    private static Context mContext;
    private static  Activity mActivity;
    private APIInterface apiInterface;
    private static OnLoginCallbackListener onLogincallbackDialogListener;
    String mesage;
    FragmentLoginDialogBinding mBinding;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";

    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=101;
    private boolean isLoggedin=false;
    static  boolean fromHomeActivity=false;
    private final int GOOGLE_LOGIN_CANECLED=13;
    private final int GOOGLE_LOGIN_FAILURE=7;
    Set<String> deniedPermissions;


    public static LoginDialogfragment getInstance(Context context,Activity activity, OnLoginCallbackListener mListener){
        mActivity=activity;
        mContext=context;
        onLogincallbackDialogListener=mListener;
        fromHomeActivity=false;
        return new LoginDialogfragment();
    }
    public static LoginDialogfragment getInstance(boolean controll_from,Context context,Activity activity, OnLoginCallbackListener mListener){
        mActivity=activity;
        mContext=context;
        onLogincallbackDialogListener=mListener;
        fromHomeActivity=controll_from;
        return new LoginDialogfragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  mContext=getContext();
        //mActivity=getActivity();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        callbackManager = CallbackManager.Factory.create();
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_login_dialog, container, false);
        view= mBinding.getRoot();
        mBinding.whatWantToCheck.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shaking_anim));
        mesage=getArguments().getString("message");



        mBinding.loginForWhat.setText(mesage);
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onLogincallbackDialogListener.OnCancel();
        }
        });
        mBinding.tvFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLogin();
            }
        });
        init_GLogin();

        return view;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //  setStyle(R.style.popup_window_animation,getTheme());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.popup_window_animation;
    }

    /**
     * Initialize Google login
     */
    private void init_GLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        // updateUI(account);

       /* SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);*/

        mBinding.tvGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void showMailRequiredMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.email_mandatory)
                .setPositiveButton("Ok!Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
// FIRE ZE MISSILES!
                        dialog.dismiss();

                    }
                });

        builder.create().show();
    }
    /**
     * Initialize facebook login
     *
     */
    public void fbLogin()
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
                        deniedPermissions=  AccessToken.getCurrentAccessToken().getDeclinedPermissions();
                        if (deniedPermissions.contains(EMAIL)) {
                            showEmailMandatoryPopop();
                        }
                        deniedPermissions=null;


                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            String email = object.getString("email");
                                            String name = object.getString("name");

                                            //  String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                            String id = object.getString("id");
                                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=large";
                                           AppLog.getInstance().printLog(mContext,"context:::"+email);
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
                        AppLog.getInstance().printToast(mContext,"login cancelled::");
                        deniedPermissions=  AccessToken.getCurrentAccessToken().getDeclinedPermissions();
                        if (deniedPermissions.contains(EMAIL)) {
                            AppLog.getInstance().printToast(mContext,"Without your email we're not able to create your account.");
                            deniedPermissions=null;
                        }else{
                            AppLog.getInstance().printToast(mContext,"Login Cancelled");

                        }
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        // App code
                        AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_error));
                        if(exception.toString().equalsIgnoreCase("User logged in as different Facebook user."))
                            AppLog.getInstance().printToast(mContext,"Something went wrong with facebook login.Please try google login.");
                        else
                            AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_error));
                    }
                });
    }
    public void showEmailMandatoryPopop(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                makeLoginReq(personName,personEmail,personPhoto== null? "":personPhoto.toString() ,personId,GlobalConstant.getInstance().LOGIN_TYPE_GOOGLE);
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

            AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_error));
            //updateUI(null);
        }
    }
    private void makeLoginReq(String name,String email,String profile_img,String social_id,String login_type) {
        final Dialog pDialog = new Dialog(mActivity, R.style.NewDialog);
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
               isLoggedin=true;
                pDialog.dismiss();
                User user = response.body();

                SharedPreferences.getInstance(mContext).saveLoggedInUser(user.data.name,
                        user.data.email,
                        user.data.profile_pic,
                        user.data.logintype,
                        user.data.social_id,
                        user.data.student_id

                );
                dismiss();
                onLogincallbackDialogListener.onSuccess();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
            }
        });

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(!isLoggedin && !fromHomeActivity)
        getActivity().onBackPressed();
    }
}


/**
 * Developed by AbhiAndroid.com
 */