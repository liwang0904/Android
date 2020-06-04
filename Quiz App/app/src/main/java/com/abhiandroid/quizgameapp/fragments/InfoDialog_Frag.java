package com.abhiandroid.quizgameapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.model.Info_Pojo;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.google.common.base.Utf8;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Developed by AbhiAndroid.com
 */

public class InfoDialog_Frag extends DialogFragment {

    private View view;
    private Context mContext;
    private Activity mActivity;
    private APIInterface apiInterface;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  mContext=getContext();
        if(mActivity==null)
            mActivity=getActivity();
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_info_dialog, container, false);
        WebView info_rules=view.findViewById(R.id.info_rules);

        ImageView back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        getInfo(info_rules);
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
    private void getInfo(final WebView info_rules) {
        //final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        final Dialog pDialog = new Dialog(mActivity, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        Call<Info_Pojo> call1 = apiInterface.getInfo();
        call1.enqueue(new Callback<Info_Pojo>() {
            @Override
            public void onResponse(Call<Info_Pojo> call, Response<Info_Pojo> response) {
                pDialog.dismiss();
                AppLog.getInstance().printLog(mActivity,"success response");
                Info_Pojo info = response.body();
                info_rules.loadData(info.rules ,"text/html", "UTF-8");

            }

            @Override
            public void onFailure(Call<Info_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mActivity,"failed response");
                AppLog.getInstance().printToast(mActivity,getResources().getString(R.string.internet_not_available));
            }
        });
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

}


/**
 * Developed by AbhiAndroid.com
 */