package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivitySearchBinding;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.SubCategory_Pojo;
import com.abhiandroid.quizgameapp.quiz_adapters.Category_Adapter;
import com.abhiandroid.quizgameapp.quiz_adapters.SubCategory_Adapter;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AbhiAndroid.com
 */

public class Search_Activity extends ParentActicity implements InternetRefreshCallback{
    ActivitySearchBinding mBinding;
    private Context mContext;
    public static Search_Activity mActivity;
    private APIInterface apiInterface;
    private int search_type;
    private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    static int current_page,total_pages;
    List<Category_Pojo.Data> data=new ArrayList<>();
    List<SubCategory_Pojo.Data> subdata = new ArrayList<>();
    private String search_txt;
    private String cat_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        /**
         * @Initialize Databinding library and set xml layout
         */
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_search_);
        /**
         * create Retrofit Api interface object
         */
        apiInterface = APIClient.getClient().create(APIInterface.class);

        /**
         * @GET data from intent.
         */
        search_type= getIntent().getBundleExtra("bundle").getInt("search_type");
        if(search_type== GlobalConstant.getInstance().TYPE_SUBCATEGORY)
            cat_id=getIntent().getBundleExtra("bundle").getString("cat_id");
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
     * Search subcategories @Params cat_id,text
     * @param isDialogshow
     * @param search_txt
     */
    private void searchSubcategories(int isDialogshow, String search_txt) {
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);

        // pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitle("Loading");

        pDialog.setCancelable(false);
        if (isDialogshow == SHOW_PROGRESS_DIALOG)
            pDialog.show();

        if (current_page < total_pages || current_page == 0)
            current_page++;


        Call<SubCategory_Pojo> call1 = apiInterface.searchSubCategories(search_txt,current_page+"",cat_id);

        call1.enqueue(new Callback<SubCategory_Pojo>() {
            @Override
            public void onResponse(Call<SubCategory_Pojo> call, Response<SubCategory_Pojo> response) {
                pDialog.dismiss();
                mBinding.internetErrorPanel.setVisibility(View.GONE);

                AppLog.getInstance().printLog(mContext, "success response");

                SubCategory_Pojo subcategory = response.body();

                current_page = subcategory.meta.current_page;
                total_pages = subcategory.meta.last_page;
                if (current_page == total_pages) {
                    mBinding.progress.setVisibility(View.GONE);
                    mBinding.llLoadMore.setVisibility(View.GONE);
                } else {
                    mBinding.llLoadMore.setVisibility(View.VISIBLE);
                    mBinding.progress.setVisibility(View.GONE);
                }
                subdata.addAll(subcategory.data);
                /**
                 * Set Limit layout @Invisible and Data showing panel @Visible
                 */
                if(subdata.size()>0)
                mBinding.tvMinimumSearchLimit.setVisibility(View.INVISIBLE);
                else {
                    mBinding.tvMinimumSearchLimit.setText(getResources().getString(R.string.no_subcat_found));
                    mBinding.tvMinimumSearchLimit.setVisibility(View.VISIBLE);
                }
                mBinding.searchedDataPanel.setVisibility(View.VISIBLE);

                init_subAdapter(subcategory, subdata);


            }

            private void init_subAdapter(SubCategory_Pojo subcategory, List<SubCategory_Pojo.Data> subdata) {
                GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 4);

                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return 2;
                    }
                });
                mBinding.rvCategories.setLayoutManager(layoutManager);
                SubCategory_Adapter  mAdapter = new SubCategory_Adapter(mContext,mActivity, subcategory, subdata);
                mBinding.rvCategories.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<SubCategory_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mContext, "failed response");
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(Search_Activity.this);
            }
        });
    }

    /**
     * Search Categories @Param text.
     * @param isDialogshow
     * @param search_txt
     */
    private void searchCategories(int isDialogshow, String search_txt) {

        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        if(isDialogshow==SHOW_PROGRESS_DIALOG)
            pDialog.show();

        if(current_page<total_pages || current_page==0)
            current_page++;


        Call<Category_Pojo> call1 = apiInterface.searchCategories(search_txt,current_page+"");
        call1.enqueue(new Callback<Category_Pojo>() {
            @Override
            public void onResponse(Call<Category_Pojo> call, Response<Category_Pojo> response) {
                pDialog.dismiss();
                mBinding.internetErrorPanel.setVisibility(View.GONE);

                AppLog.getInstance().printLog(mContext,"success response");

                Category_Pojo category = response.body();

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
                    mBinding.tvMinimumSearchLimit.setText(getResources().getString(R.string.no_categories_found));
                    mBinding.tvMinimumSearchLimit.setVisibility(View.VISIBLE);
                }
                mBinding.searchedDataPanel.setVisibility(View.VISIBLE);


                init_Adapter(category,data);


            }

            @Override
            public void onFailure(Call<Category_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mContext,"failed response");
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(Search_Activity.this);
            }
        });
    }
    public void loadMore(View viewGroup){
        mBinding.llLoadMore.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        if(search_type== GlobalConstant.getInstance().TYPE_CATEGORY)
            searchCategories(HIDE_PROGRESS_DIALOG, search_txt);
        else
            searchSubcategories(HIDE_PROGRESS_DIALOG, search_txt);
    }
    public void goBack(View view){
        if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
            showInterstitialAds();
        finish();
    }
    public void search(View view){
        data.clear();
        subdata.clear();
        total_pages=current_page=0;
         search_txt=mBinding.searchView.getText().toString().trim();
        if(search_txt.length()>2){
            if(search_type== GlobalConstant.getInstance().TYPE_CATEGORY)
                searchCategories(SHOW_PROGRESS_DIALOG,search_txt);
            else
                searchSubcategories(SHOW_PROGRESS_DIALOG,search_txt);
        }
        else {
            mBinding.tvMinimumSearchLimit.setVisibility(View.VISIBLE);
            mBinding.tvMinimumSearchLimit.setText(getResources().getString(R.string.minimum_search_limit));
            mBinding.searchedDataPanel.setVisibility(View.INVISIBLE);
        }
    }
    private void init_Adapter(Category_Pojo category, List<Category_Pojo.Data> data) {
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        mBinding.rvCategories.setLayoutManager(layoutManager);
        Category_Adapter  mAdapter = new Category_Adapter(mContext,category,data,mActivity);
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