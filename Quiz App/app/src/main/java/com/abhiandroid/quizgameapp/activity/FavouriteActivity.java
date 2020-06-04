package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityQuizBinding;
import com.abhiandroid.quizgameapp.fragments.LoginDialogfragment;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.interfaces.OnLoginCallbackListener;
import com.abhiandroid.quizgameapp.model.QuizList_Pojo;
import com.abhiandroid.quizgameapp.model.User;
import com.abhiandroid.quizgameapp.quiz_adapters.QuizList_Adapter;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.abhiandroid.quizgameapp.utils.RecyclerItemClickListener;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AbhiAndroid.com
 */

public class FavouriteActivity extends com.abhiandroid.quizgameapp.activity.ParentActicity implements InternetRefreshCallback {
    private LinearLayoutManager layoutManager;
    private QuizList_Adapter mAdapter;
    private APIInterface apiInterface;
    private Context mContext;
    public static Activity mActivity;
    private List<QuizList_Pojo.Data> data=new ArrayList<>();
    private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    private ActivityQuizBinding mBinding;
    private String subcat_id;
    private CallbackManager callbackManager;

    private String quiz_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        callbackManager = CallbackManager.Factory.create();
       //Get Data from Intent.

        /**
         * Refresh Data and variables
         */

        apiInterface = APIClient.getClient().create(APIInterface.class);
        init_Header();
        //Get categories from api

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CommonUtils.getInstance().isUserLoggedIn(mContext)) {
            getQuizList(SHOW_PROGRESS_DIALOG);
        }else{
            LoginDialogfragment newFragment =LoginDialogfragment.getInstance(mContext,mActivity, new OnLoginCallbackListener() {
                @Override
                public void OnCancel() {
                    finish();
                }

                @Override
                public void onSuccess() {
                    getQuizList(SHOW_PROGRESS_DIALOG);
                }
            });
            Bundle bundle=new Bundle();
            bundle.putString("message",getResources().getString(R.string.login_mandotry_leaderboard));
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }

    private void getQuizList(int isDialogshow) {
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);

        if(isDialogshow==SHOW_PROGRESS_DIALOG)
            pDialog.show();

        String user_id=SharedPreferences.getInstance(mContext).getLoggedUserData(GlobalConstant.getInstance().STUDENT_ID);
        AppLog.getInstance().printLog(mContext,"User id:::"+user_id);

        Call<QuizList_Pojo> call1 = apiInterface.userFavourites(user_id);
        AppLog.getInstance().printLog(mContext,"call method");
        call1.enqueue(new Callback<QuizList_Pojo>() {
            @Override
            public void onResponse(Call<QuizList_Pojo> call, Response<QuizList_Pojo> response) {
                pDialog.dismiss();
                data.clear();
                mBinding.internetErrorPanel.setVisibility(View.GONE);
                AppLog.getInstance().printLog(mContext,"success response");

                QuizList_Pojo quiz = response.body();


                data.addAll(quiz.data);
                /**
                 * set views visibility according to data foundation.
                 */
                if(data.size()>0){
                    mBinding.searchedDataPanel.setVisibility(View.VISIBLE);
                    mBinding.noDataFoundPanel.setVisibility(View.GONE);
                }else {
                    mBinding.searchedDataPanel.setVisibility(View.GONE);
                    mBinding.noDataFoundPanel.setVisibility(View.VISIBLE);
                    mBinding.tryAnotherCat.setVisibility(View.GONE);
                    mBinding.noDataFound.setText(getResources().getString(R.string.no_favourite_found));
                }
                init_Adapter(quiz,data);


            }

            @Override
            public void onFailure(Call<QuizList_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mContext,"failed response");
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(FavouriteActivity.this);
            }
        });
    }
    private void init_Adapter(QuizList_Pojo quiz, final List<QuizList_Pojo.Data> data) {
        layoutManager = new LinearLayoutManager(this);
        mBinding.rvQuizes.setLayoutManager(layoutManager);
        mAdapter = new QuizList_Adapter(mContext,quiz,data,mActivity);
        mBinding.rvQuizes.setAdapter(mAdapter);
        mBinding.rvQuizes.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        AppLog.getInstance().printLog(mContext,"clciked");
                        quiz_id=data.get(position).quiz_id;
                        CommonUtils.getInstance().setRippleEffect(view);

                        Bundle bundle=new Bundle();
                        bundle.putString("intent_from",  GlobalConstant.getInstance().TYPE_OTHER_SCREEN);
                        bundle.putString("quiz_id",data.get(position).quiz_id);
                        mActivity.startActivity(new Intent(mActivity, com.abhiandroid.quizgameapp.activity.StartQuizActivity.class).putExtra("bundle",bundle));

                    }
                })
        );
    }
    private void init_Header() {

        mBinding.searchToolbar.setImage(getResources().getDrawable(R.drawable.back_icon2));
        mBinding.searchToolbar.setText(getResources().getString(R.string.favorite));
        mBinding. searchToolbar.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.getInstance().setRippleEffect(mBinding. searchToolbar.imageView);
                if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
                    showInterstitialAds();
               finish();
            }
        });
        mBinding.searchToolbar.search_icon.setVisibility(View.GONE);
    }
    @Override
    public void onInternetrefresh() {
        //Get categories from api
        getQuizList(SHOW_PROGRESS_DIALOG);
    }

}

/**
 * Created by AbhiAndroid.com
 */