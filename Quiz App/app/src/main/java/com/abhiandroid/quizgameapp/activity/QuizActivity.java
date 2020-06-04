package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityCategoryBinding;
import com.abhiandroid.quizgameapp.databinding.ActivityQuizBinding;
import com.abhiandroid.quizgameapp.fragments.LoginDialogfragment;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.interfaces.OnLoginCallbackListener;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.QuizList_Pojo;
import com.abhiandroid.quizgameapp.model.User;
import com.abhiandroid.quizgameapp.quiz_adapters.Category_Adapter;
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

public class QuizActivity extends ParentActicity implements InternetRefreshCallback{
    private LinearLayoutManager layoutManager;
    private QuizList_Adapter mAdapter;
    private APIInterface apiInterface;
    private Context mContext;
    public static QuizActivity mActivity;
    private static int current_page=0,total_pages=0;
    private List<QuizList_Pojo.Data> data=new ArrayList<>();
    private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    private ActivityQuizBinding mBinding;
    private String subcat_id;

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String USER_BIRTHDAY = "user_birthday";
    private static final String USER_FRIENDS = "user_friends";
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=101;
    private String quiz_id;
    private String intent_from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        callbackManager = CallbackManager.Factory.create();
       //Get Data from Intent.
        subcat_id=getIntent().getBundleExtra("bundle").getString("subcat_id");
        intent_from=getIntent().getBundleExtra("bundle").getString("intent_from");

        /**
         * Refresh Data and variables
         */
        current_page=total_pages=0;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        init_Header();
        //Get categories from api
        getQuizList(SHOW_PROGRESS_DIALOG);
    }

    private void getQuizList(int isDialogshow) {
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);

        if(isDialogshow==SHOW_PROGRESS_DIALOG)
            pDialog.show();

        if(current_page<total_pages || current_page==0)
            current_page++;


        Call<QuizList_Pojo> call1 = apiInterface.getQuizes(subcat_id,current_page+"");

        call1.enqueue(new Callback<QuizList_Pojo>() {
            @Override
            public void onResponse(Call<QuizList_Pojo> call, Response<QuizList_Pojo> response) {
                pDialog.dismiss();
                mBinding.internetErrorPanel.setVisibility(View.GONE);

                AppLog.getInstance().printLog(mContext,"success response");

                QuizList_Pojo quiz = response.body();

                current_page=quiz.meta.current_page;
                total_pages=quiz.meta.last_page;
                if(current_page==total_pages){
                    mBinding.progress.setVisibility(View.GONE);
                    mBinding.llLoadMore.setVisibility(View.GONE);
                }else{
                    mBinding.llLoadMore.setVisibility(View.VISIBLE);
                    mBinding.progress.setVisibility(View.GONE);
                }
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
                    mBinding.tryAnotherCat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          //  QuizActivity. super.sendIntent(CategoryActivity.class,null);
                            SubcategoryActivity.mActivity.finish();
                            if(Search_Activity.mActivity!=null)
                            Search_Activity.mActivity.finish();
                            finish();
                        }
                    });
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
                mBinding.internetErrorPanel.setOnRefreshListener(QuizActivity.this);
            }
        });
    }
    private void init_Adapter(QuizList_Pojo quiz, final List<QuizList_Pojo.Data> data) {
        layoutManager = new LinearLayoutManager(this);
        mBinding.rvQuizes.setLayoutManager(layoutManager);
        mAdapter = new QuizList_Adapter(mContext,quiz,data,mActivity);

        mBinding.rvQuizes.setAdapter(mAdapter);

    }



    private void init_Header() {

        mBinding.searchToolbar.setImage(getResources().getDrawable(R.drawable.back_icon2));
        mBinding.searchToolbar.setText(getResources().getString(R.string.choose_quiz));
        mBinding. searchToolbar.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.getInstance().setRippleEffect(mBinding. searchToolbar.imageView);
                if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
                    showInterstitialAds();
                if(intent_from.equalsIgnoreCase(GlobalConstant.getInstance().TYPE_NOTIFICATION))
                    QuizActivity.super.sendIntent(CategoryActivity.class,null);
                finish();
            }
        });
        mBinding.searchToolbar.search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("subcat_id",subcat_id);
                QuizActivity.super.sendIntent(Quiz_Search_Activity.class,bundle);
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        current_page=0;
        total_pages=0;
        if(intent_from.equalsIgnoreCase(GlobalConstant.getInstance().TYPE_NOTIFICATION))
            QuizActivity.super.sendIntent(CategoryActivity.class,null);
    }
    public void loadMore(View viewGroup){
        mBinding.llLoadMore.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        getQuizList(HIDE_PROGRESS_DIALOG);
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