package com.abhiandroid.quizgameapp.activity;

/**
 * Created by AbhiAndroid.com
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityCategoryBinding;
import com.abhiandroid.quizgameapp.fragments.LoginDialogfragment;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.interfaces.OnLoginCallbackListener;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.quiz_adapters.Category_Adapter;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.abhiandroid.quizgameapp.utils.Search_ToolBar;
import com.abhiandroid.quizgameapp.views.Internet_error_panel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Boolean.getBoolean;

public class CategoryActivity extends com.abhiandroid.quizgameapp.activity.ParentActicity implements InternetRefreshCallback{
    private LinearLayoutManager layoutManager;
    private Category_Adapter mAdapter;
    private ActivityCategoryBinding mBinding;
    private APIInterface apiInterface;
    private Context mContext;
    public static CategoryActivity mActivity;
    static int current_page=0,total_pages=0;
    List<Category_Pojo.Data> data=new ArrayList<>();
    private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private int backPresssed=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        mActivity=this;
        /**
         * Refresh Data and variables
         */
        current_page=total_pages=0;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_category);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        init_Header();


    }

    @Override
    protected void onResume() {
        super.onResume();
        current_page=total_pages=0;
        data.clear();
        mBinding.llLoadMore.setVisibility(View.GONE);
        getCategories(SHOW_PROGRESS_DIALOG);
        init_Drawer();

    }

    private void init_Drawer() {
       //Create instances of drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });
        //Get access for drawer view's
        if(CommonUtils.getInstance().isUserLoggedIn(mContext)) {
            View view = navigationView.getHeaderView(0);
            ImageView imageView = view.findViewById(R.id.iv_user_image);
            TextView username = view.findViewById(R.id.tv_user_name);
            TextView email = view.findViewById(R.id.tv_user_email);
            SharedPreferences prefs=SharedPreferences.getInstance(mContext);
            GlobalConstant mCons=GlobalConstant.getInstance();
           // username.setText(prefs.getLoggedUserData(mCons.NAME));
           // email.setText(prefs.getLoggedUserData(mCons.EMAIL));
            /**
             * Set profile image <Not required> uncomment code for set profile image.
             */
          //  Picasso.with(CategoryActivity.this).load(prefs.getLoggedUserData(mCons.IMAGE)).fit().placeholder(R.drawable.green_placeholder).into(imageView);
        }
        /**
         * Set visibility Login and Logout button according user availability.
         */
        if(CommonUtils.getInstance().isUserLoggedIn(mContext)) {
           // navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_favourite).setVisible(true);
        }
        else {
          //  navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_favourite).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_login:
                                    login();

                                break;
                            case R.id.nav_favourite:
                                CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.FavouriteActivity.class, null);
                                break;
                            case R.id.nav_leaderboard:
                                goToLeaderBoard();
                                //CategoryActivity.super.sendIntent(LeaderBoardActivity.class, null);
                                break;
                                case R.id.nav_profile:
                                    goToProfile();

                                break;
                                case R.id.nav_faq:
                                CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.FaqActivity.class, null);
                                break;
                                case R.id.nav_terms:
                                CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.TermsActivity.class, null);
                                break;
                                case R.id.nav_setting:
                                CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.SettingActivity.class, null);
                                break;
                                case R.id.nav_rateapp:
                                rateAppOnPlayStore();
                                break;
                                case R.id.nav_feedback:
                                    Intent email = new Intent(android.content.Intent.ACTION_SEND);
                                    email.setType("text/html");
                                    if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N)
                                    email.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmailExternal");
                                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{GlobalConstant.getInstance().FEEDBACK_EMAIL});
                                    email.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.app_name)+" feedback");
                                    //need this to prompts email client only

                                    startActivity(email);
                                break;



                        }

                        return true;
                    }
                });
    }

    private void goToProfile() {
        if(CommonUtils.getInstance().isUserLoggedIn(mContext))
        CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.ProfileActivity.class, null);
        else{
            LoginDialogfragment newFragment =LoginDialogfragment.getInstance(true,mContext,mActivity, new OnLoginCallbackListener() {
                @Override
                public void OnCancel() {

                }

                @Override
                public void onSuccess() {
                    CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.ProfileActivity.class, null);
                    AppLog.getInstance().printToast(mContext,getResources().getString(R.string.logged_in_successfully));
                }
            });
            Bundle bundle=new Bundle();
            bundle.putString("message",getResources().getString(R.string.profile_login_message));
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }
    private void goToLeaderBoard() {
        if(CommonUtils.getInstance().isUserLoggedIn(mContext))
            CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.LeaderBoardActivity.class, null);
        else{
            LoginDialogfragment newFragment =LoginDialogfragment.getInstance(true,mContext,mActivity, new OnLoginCallbackListener() {
                @Override
                public void OnCancel() {

                }

                @Override
                public void onSuccess() {
                    CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.LeaderBoardActivity.class, null);
                    AppLog.getInstance().printToast(mContext,getResources().getString(R.string.logged_in_successfully));
                }
            });
            Bundle bundle=new Bundle();
            bundle.putString("message",getResources().getString(R.string.login_mandotry_leaderboard));
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }

    private void rateAppOnPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            AppLog.getInstance().printToast(mContext," unable to find market app");

        }
    }
    private void login() {
        LoginDialogfragment newFragment =LoginDialogfragment.getInstance(true,mContext,mActivity, new OnLoginCallbackListener() {
            @Override
            public void OnCancel() {

            }

            @Override
            public void onSuccess() {
                init_Drawer();
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.logged_in_successfully));
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("message",getResources().getString(R.string.please_login));
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void init_Header() {

        mBinding.searchToolbar.setImage(getResources().getDrawable(R.drawable.menu_icon));
        mBinding.searchToolbar.setText(getResources().getString(R.string.choose_category));
        mBinding. searchToolbar.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.getInstance().setRippleEffect(mBinding. searchToolbar.imageView);
               handleDrawer();
            }
        });
        mBinding.searchToolbar.search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("search_type", GlobalConstant.getInstance().TYPE_CATEGORY);
                CategoryActivity.super.sendIntent(com.abhiandroid.quizgameapp.activity.Search_Activity.class,bundle);
            }
        });

    }
    public void handleDrawer(){
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            mDrawerLayout.closeDrawers();
        else
            mDrawerLayout.openDrawer(Gravity.LEFT);
    }
    public void loadMore(View viewGroup){
        mBinding.llLoadMore.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        getCategories(HIDE_PROGRESS_DIALOG);
    }
    private void getCategories(int isDialogshow) {
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);

        if(isDialogshow==SHOW_PROGRESS_DIALOG)
            pDialog.show();

        if(current_page<total_pages || current_page==0)
            current_page++;


        Call<Category_Pojo> call1 = apiInterface.getCategories(current_page+"");

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
                 * set views visibility according to data foundation.
                 */

                if(data.size()>0){
                    mBinding.searchedDataPanel.setVisibility(View.VISIBLE);
                    mBinding.noDataFound.setVisibility(View.GONE);
                }else {
                    mBinding.searchedDataPanel.setVisibility(View.GONE);
                    mBinding.noDataFound.setVisibility(View.VISIBLE);
                    mBinding.noDataFound.setText(getResources().getString(R.string.no_categories_added));
                }

                init_Adapter(category,data);


            }

            @Override
            public void onFailure(Call<Category_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mContext,"failed response");
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(CategoryActivity.this);
            }
        });
    }

    private void init_Adapter(Category_Pojo category, List<Category_Pojo.Data> data) {
        layoutManager = new LinearLayoutManager(this);
        mBinding.rvCategories.setLayoutManager(layoutManager);
        mAdapter = new Category_Adapter(mContext,category,data,mActivity);
        mBinding.rvCategories.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        backPresssed=0;
    }

    @Override
    public void onBackPressed() {
        if(backPresssed<1) {
            backPresssed++;
            AppLog.getInstance().printToast(mContext,"Press BACK again to exit");
        }else
        super.onBackPressed();
        current_page=0;
        total_pages=0;

    }

    @Override
    public void onInternetrefresh() {
        getCategories(SHOW_PROGRESS_DIALOG);
    }
}

/**
 * Created by AbhiAndroid.com
 */