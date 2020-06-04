package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityPlayQuizBinding;
import com.abhiandroid.quizgameapp.interfaces.WebViewClickListener;
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
import com.abhiandroid.quizgameapp.utils.QuizVibrator;
import com.abhiandroid.quizgameapp.views.MyWebViewPanel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AbhiAndroid.com
 */

public class PlayQuizActivity extends ParentActicity {
    /**
     * Field are manually editable. you can change according your need.
     */
    private final int RIGHT_ANSWER_SCORE=20;
    private final int WRONG_ANSWER_SCORE=-2;
    private  int chance_50_50=1;
    private  int chance_skip=1;
    private  final int WRONG_ANS_WAIT_TIME=3000;
    private  final int RIGHT_ANS_WAIT_TIME=2000;
    //////////////////////////

    private AppDatabase db;
    private ActivityPlayQuizBinding mBinding;
    private APIInterface apiInterface;
    private Context mContext;
    private Activity mActivity;
    private String quiz_name,quiz_id,quiz_image;
    private int question_time,lifes;
    List<Quiz> question_list;
    private GlobalConstant globalConstant;
    private int question_no=-1;
    private  CountDownTimer countDownTimer;
    //make all click false while showing answer;
    private boolean hold_All_click=true;
    private boolean isControlLose=false;
    private int total_right_ans=0;
    private int total_wrong_ans=0;
    private int total_skipped_ans=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Get Activity and Context class instances
         */
        mContext=getApplicationContext();
        mActivity=this;

        //Initialize DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_quiz);

        // Get Retrofit ApiClient class instance.
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //Get Database class instance.
        db= MySingleton.getInstance().getRoomInstance();

        question_list=db.userDao().getAll();

        //Create instance for "GlobalConstant" class.
        globalConstant=GlobalConstant.getInstance();

        /**
         * Receive Intent data
         */
        lifes=Integer.parseInt(getIntent().getBundleExtra("bundle").getString("lifes"));
        quiz_name=getIntent().getBundleExtra("bundle").getString("quiz_name");
        question_time=Integer.parseInt(getIntent().getBundleExtra("bundle").getString("question_time"));
        quiz_image=getIntent().getBundleExtra("bundle").getString("quiz_image");
        quiz_id=getIntent().getBundleExtra("bundle").getString("quiz_id");

        refreshViewsAtStarting();
        init_HeaderViews();
        showquestion();

    }

    /**
     * Initialize Header view and set Default values.
     */
    private void init_HeaderViews() {
        mBinding.tvQuizname.setText(quiz_name);
        mBinding.tvLifes.setText("x"+lifes);
        mBinding.tvScore.setText("0");

    }


    /**
     * @OnClick back arrow "ImageView"
     * @param view
     */
    public void goBack(View view){
        if (!isControlLose) {
            if(countDownTimer!=null)
                countDownTimer.cancel();
            isControlLose = true;
            //  refreshViews();
            //finish();
        }
       /* if(countDownTimer!=null)
            countDownTimer.cancel();*/
        if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
            showInterstitialAds();

        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void showquestion() {
        hold_All_click=true;
        //Enable clicklistener for all options.
        mBinding.wvOptionA.setClickable(true);

        // Increase counter for next question.
        ++question_no;

        //Check wether question is < listsize if yes then show next question otherwise Redirect to Score Activity.
        if (question_no < question_list.size()){
            updateTimer();

            //Set Data on Views.
            mBinding.tvCurrentQuestion.setText("Ques " + getQuestionNo(question_no));

            //Show Image for questions if exists. otherwise set "setVisibility(View.GONE);" of imageview.
            if (question_list.get(question_no).getType().equalsIgnoreCase(globalConstant.QUESTION_TYPE_REGULAR))
                mBinding.ivQuestionimage.setVisibility(View.GONE);
            else {
                mBinding.ivQuestionimage.setVisibility(View.VISIBLE);

                byte[] array=question_list.get(question_no).getImage_array();
                Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
                //Picasso.with(mContext).load((array)).fit().placeholder(R.drawable.image_placeholder).into(mBinding.ivQuestionimage);
                mBinding.ivQuestionimage.setImageBitmap(bitmap);
            }

            //Set Questions
            mBinding.wvQuestion.loadDataWithBaseURL(null, question_list.get(question_no).getTitle(), "text/html", "utf-8", null);


            /**
             * Get options from Room database
             */
            ArrayList<Options> options = Converters.fromString(question_list.get(question_no).getOptions());
       /* for (int i = 0; i < options.size(); i++) {
            AppLog.getInstance().printLog(mContext, options.get(i).getOptn());
        }*/

            //Initialize options.
            mBinding.wvOptionA.loadDataInWebView(options.get(0).getOptn());
            mBinding.wvOptionA.setOnClickWindow(new WebViewClickListener() {
                @Override
                public void onClick() {

                    showAnswer(getResources().getString(R.string.option_a), mBinding.wvOptionA);
                }
            });

            mBinding.wvOptionB.loadDataInWebView(options.get(1).getOptn());
            mBinding.wvOptionB.setOnClickWindow(new WebViewClickListener() {
                @Override
                public void onClick() {

                    showAnswer(getResources().getString(R.string.option_b), mBinding.wvOptionB);
                }
            });

            mBinding.wvOptionC.loadDataInWebView(options.get(2).getOptn());
            mBinding.wvOptionC.setOnClickWindow(new WebViewClickListener() {
                @Override
                public void onClick() {

                    showAnswer(getResources().getString(R.string.option_c), mBinding.wvOptionC);
                }
            });

            mBinding.wvOptionD.loadDataInWebView(options.get(3).getOptn());
            mBinding.wvOptionD.setOnClickWindow(new WebViewClickListener() {
                @Override
                public void onClick() {

                    showAnswer(getResources().getString(R.string.option_d), mBinding.wvOptionD);
                }
            });
        }
        else {
            Bundle bundle=new Bundle();
            bundle.putString("from","show question");
            bundle.putInt("wrong_ans",total_wrong_ans);
            bundle.putInt("right_ans",total_right_ans);
            bundle.putInt("skipped_ans",total_skipped_ans);
            bundle.putString("quiz_id",quiz_id);
            bundle.putString("quiz_image",quiz_image);
            bundle.putString("quiz_name",quiz_name);
            bundle.putString("score",mBinding.tvScore.getText().toString().replace("x",""));
            // super.sendIntent(ShowScoreActivity.class, bundle);
            if(!isControlLose) {
                isControlLose=true;
                startActivity(new Intent(this, ShowScoreActivity.class).putExtra("bundle", bundle));
                this.finish();
            }

        }


    }


    /**
     * Show Answer wether  right or wrong.Then handle the views accordingly.
     * @param ans
     * @param view
     */
    private void showAnswer(String ans, final MyWebViewPanel view) {
        if(SharedPreferences.getInstance(mContext).isVibrationEnabled())
            QuizVibrator.getInstance(mContext).vibrate(20);
        hold_All_click=false;

        //Enable or Disable  webview
        mBinding.wvOptionA.setClickable(false);

        //Pause and Stop timer when showing result.
        countDownTimer.cancel();

        //Check the right answer and handle the UI accordingly..
        String right_ans="";
        if(question_no<question_list.size())
            right_ans=question_list.get(question_no).getAnswer();
        else{
            Bundle bundle=new Bundle();
            bundle.putString("from","show question");
            bundle.putInt("wrong_ans",total_wrong_ans);
            bundle.putInt("right_ans",total_right_ans);
            bundle.putInt("skipped_ans",total_skipped_ans);
            bundle.putString("quiz_id",quiz_id);
            bundle.putString("quiz_image",quiz_image);
            bundle.putString("quiz_name",quiz_name);
            bundle.putString("score",mBinding.tvScore.getText().toString().replace("x",""));
            // super.sendIntent(ShowScoreActivity.class, bundle);
            if(!isControlLose) {
                isControlLose=true;
                startActivity(new Intent(this, ShowScoreActivity.class).putExtra("bundle", bundle));
                this.finish();
            }
        }

        if(ans.equalsIgnoreCase(right_ans)) {
            total_right_ans++;
            view.setBgColor(getResources().getColor(R.color.colorPrimary));
            view.setTextColor();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    refreshAllViews(true,view);
                }
            }, RIGHT_ANS_WAIT_TIME);

        }
        else{
            total_wrong_ans++;

            switch (right_ans){
                case "Option A":
                    mBinding.wvOptionA.setBgColor(getResources().getColor(R.color.colorPrimary));
                    mBinding.wvOptionA.setTextColor();
                    break;
                case "Option B":
                    mBinding.wvOptionB.setBgColor(getResources().getColor(R.color.colorPrimary));
                    mBinding.wvOptionB.setTextColor();
                    break;
                case "Option C":
                    mBinding.wvOptionC.setBgColor(getResources().getColor(R.color.colorPrimary));
                    mBinding.wvOptionC.setTextColor();
                    break;
                case "Option D":

                    //mBinding.wvOptionD.setBgColor(getResources().getColor(R.color.colorPrimary));
                    mBinding.wvOptionD.setBgColor("FF0000");
                    mBinding.wvOptionD.setTextColor();
                    break;
            }
            view.setBgColor(getResources().getColor(android.R.color.holo_red_light));
            view.setTextColor();
            // mCounterThread.start();
            /**
             * Handler for waiting 3 seconds UI updation
             */
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    refreshAllViews(false,view);
                }
            }, WRONG_ANS_WAIT_TIME);

        }

    }




    private void refreshAllViews(final boolean isCorrect, final MyWebViewPanel view){
        //  mCounterThread.interrupt();


        mBinding.wvOptionA.setBgColor(getResources().getColor(android.R.color.holo_green_dark));
        if(isCorrect) {
            updateScore(RIGHT_ANSWER_SCORE);
        }
        else {
            updateScore(WRONG_ANSWER_SCORE);
            updateLifes();

        }
        mBinding.wvOptionA.setBgColor(getResources().getColor(android.R.color.white));
        refreshViews();

    }
    /**
     * Refresh All view's after complettion of question.
     */
    private void refreshViewsAtStarting() {
        mBinding.wvOptionA.setBgColor(getResources().getColor(android.R.color.white));
        mBinding.wvOptionB.setBgColor(getResources().getColor(android.R.color.white));
        mBinding.wvOptionC.setBgColor(getResources().getColor(android.R.color.white));
        mBinding.wvOptionD.setBgColor(getResources().getColor(android.R.color.white));
        changeAllOptionsVisibility(View.VISIBLE);
        mBinding.firstSpace.setVisibility(View.VISIBLE);
        mBinding.secondSpace.setVisibility(View.VISIBLE);
        mBinding.tv5050.setBackground(getResources().getDrawable(R.drawable.option_bg_image));
        mBinding.tvSkip.setBackground(getResources().getDrawable(R.drawable.option_bg_image));
        mBinding.tvRestart.setBackground(getResources().getDrawable(R.drawable.option_bg_image));

    }
    /**
     * Refresh All view's after complettion of question.
     */
    private void refreshViews() {
        mBinding.wvOptionA.setBgColor(getResources().getColor(android.R.color.white));
        mBinding.wvOptionB.setBgColor(getResources().getColor(android.R.color.white));
        mBinding.wvOptionC.setBgColor(getResources().getColor(android.R.color.white));
        mBinding.wvOptionD.setBgColor(getResources().getColor(android.R.color.white));
        changeAllOptionsVisibility(View.VISIBLE);
        mBinding.firstSpace.setVisibility(View.VISIBLE);
        mBinding.secondSpace.setVisibility(View.VISIBLE);
        showquestion();

    }
    private void changeAllOptionsVisibility(int visibility){
        mBinding.wvOptionA.setVisibility(visibility);
        mBinding.wvOptionB.setVisibility(visibility);
        mBinding.wvOptionC.setVisibility(visibility);
        mBinding.wvOptionD.setVisibility(visibility);
    }
    /**
     * Update the score board after question completion
     * @param add_score
     */
    private void updateScore(int add_score) {
        int currentScore=Integer.parseInt(mBinding.tvScore.getText().toString());
        currentScore +=add_score;
        mBinding.tvScore.setText(currentScore+"");
    }

    /**
     * Update Lifes after choose a wrong answer and left a question without giving any answer.
     */
    private void updateLifes() {
        int currentLife=Integer.parseInt(mBinding.tvLifes.getText().toString().replace("x",""));
        if(currentLife>0) {
            mBinding.tvLifes.setText("x"+ --currentLife);
        }
        else {
            Bundle bundle=new Bundle();
            bundle.putString("from","updateLifes");
            bundle.putInt("wrong_ans",total_wrong_ans);
            bundle.putInt("right_ans",total_right_ans);
            bundle.putInt("skipped_ans",total_skipped_ans);
            bundle.putString("quiz_id",quiz_id);
            bundle.putString("quiz_image",quiz_image);
            bundle.putString("quiz_name",quiz_name);

            bundle.putString("score",mBinding.tvScore.getText().toString().replace("x",""));
            // super.sendIntent(ShowScoreActivity.class, bundle);
            if(!isControlLose) {
                isControlLose = true;
                startActivity(new Intent(this, ShowScoreActivity.class).putExtra("bundle", bundle));
                this.finish();
            }
        }
        // mBinding.tvScore.setText(currentScore+"");
    }

    /**
     * Update the Timer after completion of any quesition or skip the question.(Left a question without choosing any option is included in question completion)
     */
    private void updateTimer(){
        if(countDownTimer!=null)
            countDownTimer.cancel();
        mBinding.donutProgress.setMax(question_time);
        final long total_time=question_time*1000;

        countDownTimer= new CountDownTimer(total_time, 1000) {

            public void onTick(long millisUntilFinished) {

                long remaining_sec=millisUntilFinished / 1000;
                mBinding.donutProgress.setText(remaining_sec+"");
                int progress= (int) (total_time-millisUntilFinished/1000);
                mBinding.donutProgress.setDonut_progress(progress+"");
            }

            public void onFinish() {
                mBinding.donutProgress.setText("0");
                int final_progress= (int) (total_time/1000);
                mBinding.donutProgress.setDonut_progress(final_progress+"");

                //If user didn't taken any action then start new question
                total_wrong_ans++;
                updateScore(WRONG_ANSWER_SCORE);
                updateLifes();
                refreshViews();

            }
        }.start();

    }

    /**
     * Update current question no in header panel
     * @param pos
     * @return
     */
    private String getQuestionNo(int pos){
        int total_qstns=question_list.size();
        return ++pos +"/"+total_qstns;
    }

    /**
     * Skip the question without effected any scoreboard and Lifeline.
     * Suggestion:: In this app curent skip chance is "1".For increase or Decrease the chance please use this variable :"private  int chance_skip=1;"
     * By Default it is "1".
     * @param view
     */
    public void skipQuestion(View view){

        if(hold_All_click) {
            if (chance_skip == 0)
                AppLog.getInstance().printToast(mContext, getResources().getString(R.string.Chance_skip_used));
            if (chance_skip > 0) {
                chance_skip--;
                total_skipped_ans++;
                showquestion();
            }
            if (chance_skip == 0) {
                //mBinding.tvSkip.setClickable(false);
                int alpha = 70;
                mBinding.tvSkip.setTextColor(mBinding.tvSkip.getTextColors().withAlpha(alpha));
                //   mBinding.tvSkip.setAlpha(alpha);
            }
        }
    }

    /**
     * Restart Quiz and play again.
     * @param view
     */
    public void restart(View view){
        if( hold_All_click) {
            if (!isControlLose) {
                if(countDownTimer!=null)
                    countDownTimer.cancel();
                isControlLose = true;
                //  refreshViews();
                finish();
            }
        }
    }

    /**
     * choose_50_50 option for only 2 options.
     * Suggestion: For increase or decrease the chance please use "private  int chance_50_50=1;" variable.
     * By default it is "1".
     * @param view
     */
    public void choose_50_50(View view){

        if(hold_All_click) {
            String right_ans = question_list.get(question_no).getAnswer();
            if (chance_50_50 == 0)
                AppLog.getInstance().printToast(mContext, getResources().getString(R.string.Chance_50_50_used));
            if (chance_50_50 > 0) {
                int option = 0, random = 0;
                MyWebViewPanel right_ans_view = null, other_option_view = null;
                //Initialize the variable through right answer option.
                switch (right_ans) {
                    case "Option A":
                        option = 1;
                        mBinding.secondSpace.setVisibility(View.GONE);
                        right_ans_view = mBinding.wvOptionA;
                        break;
                    case "Option B":
                        option = 2;
                        mBinding.secondSpace.setVisibility(View.GONE);
                        right_ans_view = mBinding.wvOptionB;
                        break;
                    case "Option C":
                        option = 3;
                        mBinding.firstSpace.setVisibility(View.GONE);
                        right_ans_view = mBinding.wvOptionC;
                        break;
                    case "Option D":
                        option = 4;
                        mBinding.firstSpace.setVisibility(View.GONE);
                        right_ans_view = mBinding.wvOptionD;
                        break;

                }
                //Generate a random no except the right option
                for (int i = 0; i < 5; i++) {
                    random = CommonUtils.getInstance().getRandomOption(4);
                    if (random != option)
                        break;
                    else
                        continue;
                }

                switch (random) {
                    case 1:
                        other_option_view = mBinding.wvOptionA;
                        mBinding.secondSpace.setVisibility(View.GONE);
                        break;
                    case 2:
                        other_option_view = mBinding.wvOptionB;
                        mBinding.secondSpace.setVisibility(View.GONE);
                        break;
                    case 3:
                        other_option_view = mBinding.wvOptionC;
                        mBinding.firstSpace.setVisibility(View.GONE);
                        break;
                    case 4:
                        mBinding.firstSpace.setVisibility(View.GONE);
                        other_option_view = mBinding.wvOptionD;
                        break;


                }

                changeAllOptionsVisibility(View.GONE);
                right_ans_view.setVisibility(View.VISIBLE);
                other_option_view.setVisibility(View.VISIBLE);

                //Decrement chance
                chance_50_50--;
            }
            if (chance_50_50 == 0) {
                int alpha = 70;
                mBinding.tv5050.setTextColor(mBinding.tvSkip.getTextColors().withAlpha(alpha));
                //   mBinding.tvSkip.setAlpha(alpha);
            }
        }
    }

    /**
     * Stop Timer thread when going back.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isControlLose) {
            if(countDownTimer!=null)
                countDownTimer.cancel();
            isControlLose = true;
            //  refreshViews();
            //finish();
        }
       /* if(countDownTimer!=null)
            countDownTimer.cancel();*/
    }
}

/**
 * Developed by AbhiAndroid.com
 */