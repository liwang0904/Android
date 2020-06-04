package com.abhiandroid.quizgameapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.ActivityShowScoreBinding;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.Firebase_Pojo;
import com.abhiandroid.quizgameapp.model.Leaderboard_User_Pojo;
import com.abhiandroid.quizgameapp.model.UpdateScorePojo;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.retrofit_libs.APIClient;
import com.abhiandroid.quizgameapp.retrofit_libs.APIInterface;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.firebase.client.Firebase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AbhiAndroid.com
 */

public class ShowScoreActivity extends ParentActicity {
    private ActivityShowScoreBinding mBinding;
    private APIInterface apiInterface;
    private Context mContext;
    public static ShowScoreActivity mActivity;
    private String score="0";
    private int total_right_ans,total_wrong_ans,total_skipped_ans;
    private String quiz_name,quiz_id,quiz_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Get Activity and Context class instances
         */
        mContext=getApplicationContext();
        mActivity=this;
        //Initialize DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_score);
        // Get Retrofit ApiClient class instance.
        apiInterface = APIClient.getClient().create(APIInterface.class);
       // AppLog.getInstance().printLog(mContext,getIntent().getBundleExtra("bundle").getString("from"));
       score= getIntent().getBundleExtra("bundle").getString("score");
       if(Integer.parseInt(score)<0)
           score="0";
       total_wrong_ans= getIntent().getBundleExtra("bundle").getInt("wrong_ans");
       total_right_ans= getIntent().getBundleExtra("bundle").getInt("right_ans");
       total_skipped_ans= getIntent().getBundleExtra("bundle").getInt("skipped_ans");
        quiz_name=getIntent().getBundleExtra("bundle").getString("quiz_name");
        quiz_image=getIntent().getBundleExtra("bundle").getString("quiz_image");
        quiz_id=getIntent().getBundleExtra("bundle").getString("quiz_id");
       init_Views();
       init_Piechart();
        if(!score.equalsIgnoreCase("0")) {
            updateScoreToBackend();
            updateScoreToFirebase();
        }

    }

    private void updateScoreToBackend() {
        String student_id=SharedPreferences.getInstance(mContext).getLoggedUserData(GlobalConstant.getInstance().STUDENT_ID);
        final Dialog pDialog = new Dialog(this, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
            pDialog.show();


        Call<UpdateScorePojo> call1 = apiInterface.updateScore(student_id,quiz_id,quiz_name,quiz_image,score);

        call1.enqueue(new Callback<UpdateScorePojo>() {
            @Override
            public void onResponse(Call<UpdateScorePojo> call, Response<UpdateScorePojo> response) {
                pDialog.dismiss();
                UpdateScorePojo updation = response.body();
              //  updateScoreToFirebase();
                AppLog.getInstance().printLog(mContext,"success response");


            }

            @Override
            public void onFailure(Call<UpdateScorePojo> call, Throwable t) {
                pDialog.dismiss();
                call.cancel();
                AppLog.getInstance().printLog(mContext,"failed response");
                AppLog.getInstance().printToast(mContext,getResources().getString(R.string.internet_not_available));
            }
        });
    }
    public void shareScore(View view){

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
       //
        // share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_TEXT, "I have scored "+score+" points in "+quiz_name+". Try beating my score by downloading this amazing app :\n\n"+uri.toString() );



        startActivity(Intent.createChooser(share, "Share Score!"));


    }
    private void updateScoreToFirebase() {
        final boolean[] flag = {true};
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference leaderboard_ref = database.getReference("leaderboard");
        final DatabaseReference students_ref = leaderboard_ref.child("student_id");

        // mDatabase.child("users").setValue("sumit");
        SharedPreferences mPref=SharedPreferences.getInstance(mContext);
        String student_name=mPref.getLoggedUserData(GlobalConstant.getInstance().NAME);
        final String student_id=mPref.getLoggedUserData(GlobalConstant.getInstance().STUDENT_ID);
        String student_image=mPref.getLoggedUserData(GlobalConstant.getInstance().IMAGE);
        final Leaderboard_User_Pojo user = new Leaderboard_User_Pojo(student_name,student_id,student_image,score,0,quiz_id
        ,quiz_name,quiz_image);
        final DatabaseReference quizes_ref = students_ref.child(student_id);
        final String input = "q";
        Query citiesQuery = quizes_ref.orderByKey().startAt(input).endAt(input + "\uf8ff");
        citiesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(flag[0]) {
                    List<String> students = new ArrayList<String>();
                    AppLog.getInstance().printLog(mContext, "on data changed called ");


                    int index = 0;
                    int current_index = 0;
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                       index++;
                    }
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AppLog.getInstance().printLog(mContext, "index::::" + index);
                        AppLog.getInstance().printLog(mContext, "current_index::::" + current_index);
                         students.add(postSnapshot.getValue().toString());
                        String db_student_id=postSnapshot.getKey().toString();
                        Firebase_Pojo mPojo=  postSnapshot.getValue(Firebase_Pojo.class);
                        int total_score=mPojo.total_score;
                        int score_=Integer.parseInt(score);
                        /**
                         * check wether quiz_id same for same user and previous result(Firebase database result) is less then current result then update firebase database!
                         */
                        if ( mPojo.quiz_id.equalsIgnoreCase(quiz_id) && Integer.parseInt(mPojo.quiz_score)<Integer.parseInt(score)) {

                            Map<String,Object> taskMap = new HashMap<String,Object>();
                            taskMap.put("quiz_score", score);
                            taskMap.put("total_score", score_+total_score);
                            students_ref.child(student_id).child(quiz_id).updateChildren(taskMap);
                            flag[0] =false;
                            break;
                        }
                        /**
                         * If quiz_id same for same user and previous result(Firebase database result) greater then current result then no need to update database!
                         * we are skipping this step by break the loop!
                         * < flag[0] =false;> This param has been update. but this optionally because in these condition data is not updated on firebase so this method will not be called again.</>
                         */
                        else if(mPojo.quiz_id.equalsIgnoreCase(quiz_id) && Integer.parseInt(mPojo.quiz_score)>Integer.parseInt(score)){
                            flag[0] =false;
                            break;
                        }else if( current_index==index-1 && !(mPojo.quiz_id.equalsIgnoreCase(quiz_id)) ){
                            AppLog.getInstance().printLog(mContext, "Not matched");
                            user.total_score=score_+total_score;
                            students_ref.child(student_id).child(quiz_id).setValue(user);
                            flag[0] =false;
                            break;
                        }
                        current_index++;
                        //  mDatabase.child(userId).setValue(user);
                    }
                    if (dataSnapshot.getValue() == null) {
                        AppLog.getInstance().printLog(mContext, "Dtaa base nullll");
                        user.total_score=Integer.parseInt(score);
                        students_ref.child(student_id).child(quiz_id).setValue(user);
                        flag[0] =false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void init_Piechart() {
        //mBinding.piechart.setUsePercentValues(true);
        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Integer> colors=new ArrayList<>();

        if(total_right_ans!=0) {
            yvalues.add(new Entry(total_right_ans, 0));
            xVals.add("Right");
            colors.add(getResources().getColor(R.color.txt_color_green));
        }
        if(total_wrong_ans!=0) {
            yvalues.add(new Entry(total_wrong_ans, 1));
            xVals.add("Wrong");
            colors.add(getResources().getColor(R.color.txt_color_red));

        }if (total_skipped_ans!=0) {
            yvalues.add(new Entry(total_skipped_ans, 2));
            xVals.add("Skipped");
            colors.add(getResources().getColor(R.color.txt_color_yellow));
        }

        PieDataSet dataSet = new PieDataSet(yvalues, "Quiz Results");

        PieData data = new PieData(xVals, dataSet);
        mBinding.piechart.setData(data);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.white));

        dataSet.setValueTextSize(getResources().getDimension(R.dimen._4sdp));
        dataSet.setColors(colors);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

                return ((int)value)+"";
                // return "8";
            }
        });
        mBinding.piechart.setDrawHoleEnabled(false);
        mBinding.piechart.setDescription("");
    }

    private void init_Views() {
        String player_name=SharedPreferences.getInstance(mContext).getLoggedUserData(GlobalConstant.getInstance().NAME);
        mBinding.tvTotalScore.setText(score);
        mBinding.tvCorrectAns.setText(total_right_ans+"");
        mBinding.tvWrongAns.setText(total_wrong_ans+"");
        mBinding.tvGreatPlay.setText(getResources().getString(R.string.great_play)+" "+player_name+"!" );
    }

    /**
     * @OnClick back arrow "ImageView"
     * @param view
     */
    public void goBack(View view){
        if(SharedPreferences.getInstance(getApplicationContext()).checkTimeForInterstitialsAds())
            showInterstitialAds();
        finish();
    }

    /**
     * For play again Finish the activity to go back;
     * @param view
     */
    public void playAgain(View view){
        finish();
    }
    public void checkRanking(View view){
        if(LeaderBoardActivity.mActivity!=null)
            LeaderBoardActivity.mActivity.finish();
        super.sendIntent(LeaderBoardActivity.class,null);
        if(SubcategoryActivity.mActivity!=null)
            SubcategoryActivity.mActivity.finish();
        if(Search_Activity.mActivity!=null)
            Search_Activity.mActivity.finish();
        if(QuizActivity.mActivity!=null)
            QuizActivity.mActivity.finish();
        if(Quiz_Search_Activity.mActivity!=null)
            Quiz_Search_Activity.mActivity.finish();
        if(StartQuizActivity.mActivity!=null)
            StartQuizActivity.mActivity.finish();
        if(FavouriteActivity.mActivity!=null)
            FavouriteActivity.mActivity.finish();
        finish();
    }
    public void goToCategories(View view){
        if(CategoryActivity.mActivity!=null)
            CategoryActivity.mActivity.finish();
        super.sendIntent(CategoryActivity.class,null);
        if(SubcategoryActivity.mActivity!=null)
        SubcategoryActivity.mActivity.finish();
        if(Search_Activity.mActivity!=null)
            Search_Activity.mActivity.finish();
        if(QuizActivity.mActivity!=null)
        QuizActivity.mActivity.finish();
        if(Quiz_Search_Activity.mActivity!=null)
            Quiz_Search_Activity.mActivity.finish();
        if(StartQuizActivity.mActivity!=null)
             StartQuizActivity.mActivity.finish();
        if(LeaderBoardActivity.mActivity!=null)
            LeaderBoardActivity.mActivity.finish();
        if(FavouriteActivity.mActivity!=null)
            FavouriteActivity.mActivity.finish();
        finish();
    }


}

/**
 * Created by AbhiAndroid.com
 */