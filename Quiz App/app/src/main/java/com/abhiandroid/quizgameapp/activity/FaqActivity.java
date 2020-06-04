package com.abhiandroid.quizgameapp.activity;

/**
 * Created by AbhiAndroid.com
 */

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
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.model.Faq_Pojo;
import com.abhiandroid.quizgameapp.model.Info_Pojo;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqActivity extends com.abhiandroid.quizgameapp.activity.ParentActicity implements InternetRefreshCallback{
    private APIInterface apiInterface;
    private Context mContext;
    private Activity mActivity;
    private ActivityFaqBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_faq);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        getFaqs(mBinding.wvFaqs);
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void getFaqs(final WebView info_rules) {
        //final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        final Dialog pDialog = new Dialog(mActivity);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        Call<Faq_Pojo> call1 = apiInterface.getFaq();
        call1.enqueue(new Callback<Faq_Pojo>() {
            @Override
            public void onResponse(Call<Faq_Pojo> call, Response<Faq_Pojo> response) {
                pDialog.dismiss();
                mBinding.internetErrorPanel.setVisibility(View.GONE);
                AppLog.getInstance().printLog(mActivity,"success response");
                Faq_Pojo faq = response.body();
                info_rules.loadData(faq.faq ,"text/html", "UTF-8");

            }

            @Override
            public void onFailure(Call<Faq_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mActivity,"failed response");
                AppLog.getInstance().printToast(mActivity,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(FaqActivity.this);
            }
        });
    }

    @Override
    public void onInternetrefresh() {
        getFaqs(mBinding.wvFaqs);
    }
}

/**
 * Created by AbhiAndroid.com
 */