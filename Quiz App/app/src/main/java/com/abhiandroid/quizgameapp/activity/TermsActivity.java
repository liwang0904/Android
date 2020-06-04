package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.databinding.ActivityFaqBinding;
import com.abhiandroid.quizgameapp.databinding.ActivityTermsBinding;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.model.Faq_Pojo;
import com.abhiandroid.quizgameapp.model.Terms_pojo;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AbhiAndroid.com
 */

public class TermsActivity extends ParentActicity implements InternetRefreshCallback{
    private APIInterface apiInterface;
    private Context mContext;
    private Activity mActivity;
    private ActivityTermsBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        getTerms(mBinding.wvTerms);
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void getTerms(final WebView info_rules) {
        //final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        final Dialog pDialog = new Dialog(mActivity);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        Call<Terms_pojo> call1 = apiInterface.getTerms();
        call1.enqueue(new Callback<Terms_pojo>() {
            @Override
            public void onResponse(Call<Terms_pojo> call, Response<Terms_pojo> response) {
                pDialog.dismiss();
                mBinding.internetErrorPanel.setVisibility(View.GONE);
                AppLog.getInstance().printLog(mActivity,"success response");
                Terms_pojo terms = response.body();
                info_rules.loadData(terms.terms ,"text/html", "UTF-8");

            }

            @Override
            public void onFailure(Call<Terms_pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mActivity,"failed response");
                AppLog.getInstance().printToast(mActivity,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(TermsActivity.this);
            }
        });
    }

    @Override
    public void onInternetrefresh() {
        getTerms(mBinding.wvTerms);
    }
}

/**
 * Created by AbhiAndroid.com
 */