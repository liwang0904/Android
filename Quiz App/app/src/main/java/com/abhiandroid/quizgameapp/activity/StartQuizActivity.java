package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityStartQuizBinding;
import com.abhiandroid.quizgameapp.fragments.InfoDialog_Frag;
import com.abhiandroid.quizgameapp.interfaces.InternetRefreshCallback;
import com.abhiandroid.quizgameapp.model.AddFavourite_Pojo;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.Info_Pojo;
import com.abhiandroid.quizgameapp.model.Questions_Pojo;
import com.abhiandroid.quizgameapp.model.QuizList_Pojo;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.room_database.AppDatabase;
import com.abhiandroid.quizgameapp.room_database.Converters;
import com.abhiandroid.quizgameapp.room_database.Options;
import com.abhiandroid.quizgameapp.room_database.Quiz;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.abhiandroid.quizgameapp.utils.MySingleton;
import com.abhiandroid.quizgameapp.utils.TimeTracker;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AbhiAndroid.com
 */

public class StartQuizActivity extends ParentActicity implements InternetRefreshCallback , DialogInterface.OnDismissListener{
    AppDatabase db;
    private String quiz_id;
    private APIInterface apiInterface;
    private Context mContext;
    public static Activity mActivity;
    private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    List<Questions_Pojo.Question> questions=new ArrayList<>();
    private ActivityStartQuizBinding mBinding;
    String quiz_name=null;
    private AdView mAdView;
    private String intent_from;
    private boolean isInfoDialogOpened=false;
    private boolean isQuizStarted=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_start_quiz);
        mContext=getApplicationContext();
        mActivity=this;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        db=MySingleton.getInstance().getRoomInstance();
        quiz_id=getIntent().getBundleExtra("bundle").getString("quiz_id");
        intent_from=getIntent().getBundleExtra("bundle").getString("intent_from");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isQuizStarted=false;
        getQuizQuestions(SHOW_PROGRESS_DIALOG);
    }

    /**
     * Initialize views by values
     * @param best_score
     * @param life
     * @param question_time
     * @param quiz_image
     * @param quiz_name
     */
    private void init_Views(String best_score, String life, String question_time, String quiz_image, String quiz_name, String followstatus) {
        Picasso.with(mContext).load(quiz_image).fit().placeholder(R.drawable.green_placeholder).into(mBinding.ivQuizImage);
        if(best_score.equalsIgnoreCase("0"))
            mBinding.tvBestScore.setText("N/A");
        else
            mBinding.tvBestScore.setText(best_score);
        int time_=Integer.parseInt(question_time);
        time_=time_*questions.size();
        TimeTracker tracker=new TimeTracker();
        String final_time=tracker.timeConversion(time_);
        mBinding.tvQuizTime.setText(final_time+"m");
        int total_question=questions.size();
        mBinding.tvTotalQuestions.setText(total_question+"");
        mBinding.tvQuizname.setText(quiz_name);
        this.quiz_name=quiz_name;
        if(followstatus.equalsIgnoreCase("0"))
            mBinding.tvFollow.setText(getResources().getString(R.string.follow));
        else
            mBinding.tvFollow.setText(getResources().getString(R.string.unfollow));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(intent_from.equalsIgnoreCase(GlobalConstant.getInstance().TYPE_NOTIFICATION))
            StartQuizActivity.super.sendIntent(CategoryActivity.class,null);

    }

    /**
     * @OnClick back arrow "ImageView"
     * @param view
     */
    public void goBack(View view){
        if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
            showInterstitialAds();
        if(intent_from.equalsIgnoreCase(GlobalConstant.getInstance().TYPE_NOTIFICATION)) {
            StartQuizActivity.super.sendIntent(CategoryActivity.class, null);

        }
        finish();
    }

    /**
     * play quiz button listener
     */
    public void playNow(String lifes, String quiz_name, String question_time,String quizImage,String quizId){
        if(!isQuizStarted) {
            isQuizStarted=true;
            Bundle bundle = new Bundle();
            bundle.putString("lifes", lifes);
            bundle.putString("quiz_name", quiz_name);
            bundle.putString("quiz_id", quizId);
            bundle.putString("quiz_image", quizImage);
            bundle.putString("question_time", question_time);
            super.sendIntent(PlayQuizActivity.class, bundle);
        }
    }

    /**
     * show info LinearLayout click Listener
     * @param view
     */
    public void showInfo(View view){
        if(!isInfoDialogOpened) {
            isInfoDialogOpened=true;
            DialogFragment newFragment = new InfoDialog_Frag();
            newFragment.show(getFragmentManager(), "dialog");
        }

    }

    private void getQuizQuestions(int isDialogshow) {

        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);

        if(isDialogshow==SHOW_PROGRESS_DIALOG)
            pDialog.show();
        String student_id=SharedPreferences.getInstance(mContext).getLoggedUserData(GlobalConstant.getInstance().STUDENT_ID);

        Call<Questions_Pojo> call1 = apiInterface.getQuestions(quiz_id,student_id);

        call1.enqueue(new Callback<Questions_Pojo>() {
            @Override
            public void onResponse(Call<Questions_Pojo> call, Response<Questions_Pojo> response) {
                pDialog.dismiss();
                questions.clear();

                mBinding.internetErrorPanel.setVisibility(View.GONE);
                AppLog.getInstance().printLog(mContext,"success response");


                final Questions_Pojo category = response.body();
                if(category.data.isquiz_delete==1){
                    AppLog.getInstance().printToast(mContext,"This quiz has been deleted");
                    finish();
                }

                questions.addAll(category.data.question);

                /**
                 * set views visibility according to data foundation.
                 */

                if(questions.size()>0){

                    mBinding.questionPanel.setVisibility(View.VISIBLE);
                    mBinding.noDataFoundPanel.setVisibility(View.GONE);
                }else {
                    mBinding.questionPanel.setVisibility(View.GONE);
                    mBinding.noDataFoundPanel.setVisibility(View.VISIBLE);

                    mBinding.tryAnotherCat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // StartQuizActivity. super.sendIntent(CategoryActivity.class,null);
                            SubcategoryActivity.mActivity.finish();
                            if(Search_Activity.mActivity!=null)
                                Search_Activity.mActivity.finish();
                            QuizActivity.mActivity.finish();
                            if(Quiz_Search_Activity.mActivity!=null)
                                Quiz_Search_Activity.mActivity.finish();
                            finish();
                        }
                    });
                }
                db.userDao().delete();


                try {
                    getByteArrayFromImage(questions,category);

                } catch (IOException e) {
                    AppLog.getInstance().printLog(mContext,"No Image Found");
                }


                init_Views(
                        category.data.best_score,
                        category.data.life,
                        category.data.question_time,
                        category.data.quiz_image,
                        category.data.quiz_name,
                        category.data.followstatus



                );
                   /* mBinding.llPlayNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Questions_Pojo.Data data=category.data;
                            playNow(data.life,data.quiz_name,data.question_time,category.data.quiz_image,category.data.quiz_id);
                        }
                    });*/
            }

            @Override
            public void onFailure(Call<Questions_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();

                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
                mBinding.internetErrorPanel.setVisibility(View.VISIBLE);
                mBinding.internetErrorPanel.setOnRefreshListener(StartQuizActivity.this);
            }
        });

    }
    public void followAction(View view){
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);


        pDialog.show();
        String student_id=SharedPreferences.getInstance(mContext).getLoggedUserData(GlobalConstant.getInstance().STUDENT_ID);

        Call<AddFavourite_Pojo> call1 = apiInterface.addFavourite(student_id,quiz_id,quiz_name);
        call1.enqueue(new Callback<AddFavourite_Pojo>() {
            @Override
            public void onResponse(Call<AddFavourite_Pojo> call, Response<AddFavourite_Pojo> response) {
                pDialog.dismiss();
                questions.clear();
                AppLog.getInstance().printLog(mContext,"success response");
                AddFavourite_Pojo mPojo=response.body();
                updateFollowButton(mPojo.message,mPojo.followstatus);

            }

            private void updateFollowButton(String message, String followstatus) {
                AppLog.getInstance().printToast(mContext,message);
                if(followstatus.equalsIgnoreCase("0"))
                    mBinding.tvFollow.setText(getResources().getString(R.string.follow));
                else
                    mBinding.tvFollow.setText(getResources().getString(R.string.unfollow));
            }

            @Override
            public void onFailure(Call<AddFavourite_Pojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
            }
        });

    }


    public void saveQuestion(Questions_Pojo.Question data, int i, byte[] byteArray, final Questions_Pojo category){

        Quiz quiz=new Quiz();
        quiz.setTitle(data.title);
        quiz.setAnswer(data.answer);
        quiz.setQuestion_id(data.question_id);
        quiz.setType(data.type);
        quiz.setImage(data.image);
        quiz.setImage_array(byteArray);
        quiz.setUid(i);
        ArrayList<Options> optionList=new ArrayList<>();
        Options option;

        int initializer = 0;
        if(data.options.size()>=4)
            initializer=data.options.size()-4;
        for(int j=initializer;j<data.options.size();j++){
            option= new Options();
            option.setId(data.options.get(j).id);
            option.setOptn(data.options.get(j).optn);
            option.setOptn_name(data.options.get(j).optn_name);
            option.setQuestion_id(data.options.get(j).question_id);
            optionList.add(option);
        }
        quiz.setOptions(Converters.fromArrayList(optionList));
        if(i==1)
            AppLog.getInstance().printLog(mContext,"option list size:::  "+Converters.fromString(quiz.getOptions()));

        db.userDao().insertAll(quiz);

        /**
         * Play quiz button listener
         */
        mBinding.llPlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Questions_Pojo.Data data=category.data;
                playNow(data.life,data.quiz_name,data.question_time,category.data.quiz_image,category.data.quiz_id);
            }
        });

    }
    private byte[] getByteArrayFromImage(List<Questions_Pojo.Question> mList, final Questions_Pojo category) throws FileNotFoundException, IOException {
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        final byte[][] byteArray = {null};
        String[] images = new String[mList.size()];
        for(int i=0;i<mList.size();i++){
            images[i]=mList.get(i).image;
        }
       /* for(int i=0;i<mList.size();i++){
           AppLog.getInstance().printLog(mContext,"list urrlllllll:::::"+images[i]);
        }*/


        new AsyncTask<String[], Void, List<Bitmap>>() {
            @Override
            protected List<Bitmap> doInBackground(String[]... params) {
                try {
                    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                    for (int i = 0; i < params[0].length; ++i) {
                        bitmaps.add(Picasso.with(mActivity).load(params[0][i]).get());
                    }

                    return bitmaps;
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            public void onPostExecute(List<Bitmap> bitmaps) {
                // if (bitmaps != null) {
                // Do stuff with your loaded bitmaps
                pDialog.dismiss();
                AppLog.getInstance().printLog(mContext,"images array size:::::"+bitmaps.size());
                for(int i=0;i<questions.size();i++){
                    if(bitmaps.get(i)!=null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        saveQuestion(questions.get(i), i, byteArray,category);
                    }else
                        saveQuestion(questions.get(i), i, null,category);
                }

                // }
            }
        }.execute(images);

        return byteArray[0];
    }
    @Override
    public void onInternetrefresh() {
        getQuizQuestions(SHOW_PROGRESS_DIALOG);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        isInfoDialogOpened=false;
    }
}

/**
 * Created by AbhiAndroid.com
 */