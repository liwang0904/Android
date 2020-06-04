package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivitySubcategoryBinding;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.SubCategory_Pojo;
import com.abhiandroid.quizgameapp.quiz_adapters.Category_Adapter;
import com.abhiandroid.quizgameapp.quiz_adapters.SubCategory_Adapter;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.Search_ToolBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AbhiAndroid.com
 */

public class SubcategoryActivity extends ParentActicity implements InternetRefreshCallback{

    private GridLayoutManager layoutManager;
    private SubCategory_Adapter mAdapter;
    private ActivitySubcategoryBinding mBinding;
    private APIInterface apiInterface;
    private Context mContext;
    public static SubcategoryActivity mActivity;
    static int current_page=0, total_pages=0;
    List<SubCategory_Pojo.Data> data = new ArrayList<>();
    private final int SHOW_PROGRESS_DIALOG = 1;
    private final int HIDE_PROGRESS_DIALOG = 0;
    private String cat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mActivity = this;
        /**
         * Refresh Data and variables
         */
        current_page=total_pages=0;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_subcategory);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        /**
         * @Get data from intent:bundle
         */
         cat_id=getIntent().getBundleExtra("bundle").getString("cat_id");
        init_Header();
        //Get categories from api
        getSubCategories(SHOW_PROGRESS_DIALOG);

    }
    private void init_Header() {
        mBinding.searchToolbar.setImage(getResources().getDrawable(R.drawable.back_icon2));
        mBinding.searchToolbar.setText(getResources().getString(R.string.choose_subcategory));
        mBinding.searchToolbar.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
                    showInterstitialAds();
                finish();
            }
        });
        mBinding.searchToolbar.search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("search_type", GlobalConstant.getInstance().TYPE_SUBCATEGORY);
                bundle.putString("cat_id", cat_id);
                SubcategoryActivity.super.sendIntent(Search_Activity.class,bundle);
            }
        });

    }
    public void loadMore(View viewGroup) {
        mBinding.llLoadMore.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);


        getSubCategories(HIDE_PROGRESS_DIALOG);
    }

    private void getSubCategories(int isDialogshow) {
       //Create dialog for progress for api implementation.
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);

        if (isDialogshow == SHOW_PROGRESS_DIALOG)
            pDialog.show();

        if (current_page < total_pages || current_page == 0)
            current_page++;

        /**
         * Create Api interface and call api.
          */
        Call<SubCategory_Pojo> call1 = apiInterface.getSubcategories(cat_id,current_page+"");
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
                data.addAll(subcategory.data);
                if(data.size()>0){
                    mBinding.searchedDataPanel.setVisibility(View.VISIBLE);
                    mBinding.noDataFound.setVisibility(View.GONE);
                }else {
                    mBinding.searchedDataPanel.setVisibility(View.GONE);
                    mBinding.noDataFound.setVisibility(View.VISIBLE);
                    mBinding.noDataFound.setText(R.string.no_subcat_added);
                }
                AppLog.getInstance().printLog(mContext, subcategory.links.next + "");
                init_Adapter(subcategory, data);
            }

            @Override
            public void onFailure(Call<SubCategory_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mContext, "failed response");
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(SubcategoryActivity.this);
            }
        });
    }

    private void init_Adapter(SubCategory_Pojo subcategory, List<SubCategory_Pojo.Data> data) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
               return 2;
            }
        });
        mBinding.rvCategories.setLayoutManager(layoutManager);
        mAdapter = new SubCategory_Adapter(mContext,mActivity, subcategory, data);
        mBinding.rvCategories.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        current_page = 0;
        total_pages = 0;
    }
    @Override
    public void onInternetrefresh() {
        getSubCategories(SHOW_PROGRESS_DIALOG);
    }
}

/**
 * Created by AbhiAndroid.com
 */