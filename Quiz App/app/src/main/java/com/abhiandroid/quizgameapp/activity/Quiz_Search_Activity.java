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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivitySearchBinding;
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


public class Quiz_Search_Activity extends ParentActicity implements InternetRefreshCallback{
    ActivitySearchBinding mBinding;
    private Context mContext;
    public static Quiz_Search_Activity mActivity;
    private APIInterface apiInterface;
    private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    static int current_page,total_pages;
    List<QuizList_Pojo.Data> data=new ArrayList<>();
    private String search_txt;
    private String subcat_id;

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String USER_BIRTHDAY = "user_birthday";
    private static final String USER_FRIENDS = "user_friends";
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=101;
    private   String quiz_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        /**
         * @Initialize Databinding library and set xml layout
         */
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_search_);
        callbackManager = CallbackManager.Factory.create();
        /**
         * create Retrofit Api interface object
         */
        apiInterface = APIClient.getClient().create(APIInterface.class);

        /**
         * @GET data from intent.
         */

            subcat_id=getIntent().getBundleExtra("bundle").getString("subcat_id");
        /**
         * @Set ime option click and handle result to search data.
         */
        mBinding.searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(mBinding.searchView);
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * Search Categories @Param text.
     * @param isDialogshow
     * @param search_txt
     */
    private void searchQuizes(int isDialogshow, String search_txt) {

        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        if(isDialogshow==SHOW_PROGRESS_DIALOG)
            pDialog.show();
        if(current_page<total_pages || current_page==0)
            current_page++;

        Call<QuizList_Pojo> call1 = apiInterface.searchQuizes(search_txt,current_page+"",subcat_id);

        call1.enqueue(new Callback<QuizList_Pojo>() {
            @Override
            public void onResponse(Call<QuizList_Pojo> call, Response<QuizList_Pojo> response) {
                pDialog.dismiss();
                mBinding.internetErrorPanel.setVisibility(View.GONE);
                QuizList_Pojo category = response.body();

                current_page=category.meta.current_page;
                total_pages=category.meta.last_page;
                if(current_page==total_pages){
                    mBinding.progress.setVisibility(View.GONE);
                    mBinding.llLoadMore.setVisibility(View.GONE);
                }else{
                    mBinding.llLoadMore.setVisibility(View.VISIBLE);
                    mBinding.progress.setVisibility(View.GONE);
                }
                data.addAll(category.data);
                /**
                 * Set Limit layout @Invisible and Data showing panel @Visible
                 */
                if(data.size()>0)
                    mBinding.tvMinimumSearchLimit.setVisibility(View.INVISIBLE);
                else {
                    mBinding.tvMinimumSearchLimit.setText(getResources().getString(R.string.no_quiz_searched));
                    mBinding.tvMinimumSearchLimit.setVisibility(View.VISIBLE);
                }
                mBinding.searchedDataPanel.setVisibility(View.VISIBLE);


                init_Adapter(category,data);


            }

            @Override
            public void onFailure(Call<QuizList_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();

                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(Quiz_Search_Activity.this);
            }
        });
    }
    public void loadMore(View viewGroup){
        mBinding.llLoadMore.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        searchQuizes(HIDE_PROGRESS_DIALOG, search_txt);
    }
    public void goBack(View view){
        if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
            showInterstitialAds();
        finish();
    }
    public void search(View view){
        data.clear();
        total_pages=current_page=0;
         search_txt=mBinding.searchView.getText().toString().trim();
        if(search_txt.length()>2){
            searchQuizes(SHOW_PROGRESS_DIALOG,search_txt);
        }
        else {
            mBinding.tvMinimumSearchLimit.setVisibility(View.VISIBLE);
            mBinding.tvMinimumSearchLimit.setText(getResources().getString(R.string.minimum_search_limit));
            mBinding.searchedDataPanel.setVisibility(View.INVISIBLE);
        }
    }
    private void init_Adapter(QuizList_Pojo category, final List<QuizList_Pojo.Data> data) {
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        mBinding.rvCategories.setLayoutManager(layoutManager);
        QuizList_Adapter mAdapter = new QuizList_Adapter(mContext,category,data,mActivity);
        mBinding.rvCategories.setAdapter(mAdapter);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        current_page=0;
        total_pages=0;
    }

    @Override
    public void onInternetrefresh() {
        search(mBinding.searchView);
    }
}

/**
 * Created by AbhiAndroid.com
 */