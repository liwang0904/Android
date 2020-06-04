package com.abhiandroid.quizgameapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.FragmentLeaderBoardBinding;
import com.abhiandroid.quizgameapp.interfaces.RecyclerItemClickListener;
import com.abhiandroid.quizgameapp.model.Firebase_Pojo;
import com.abhiandroid.quizgameapp.quiz_adapters.LeaderBoardAdapter;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Developed by AbhiAndroid.com
 */


public class LeaderBoard_Frag extends Fragment {
    static int current_page=0,total_pages=0; private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    private FragmentLeaderBoardBinding mBinding;
    private Context mContext;
    private Activity mActivity;
    private LinearLayoutManager layoutManager;
    private SharedPreferences mPref;
    private String my_student_id;
    private static boolean isToastRefresh=true;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Refresh Data and variables
         */
        current_page=total_pages=0;
        mContext=getContext();
        mActivity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_leader_board_, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        current_page=total_pages=0;
        mBinding.llLoadMore.setVisibility(View.GONE);
        getScoreFirebase();

    }
    public void loadMore(View viewGroup){
        mBinding.llLoadMore.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        getScoreFirebase();

    }

    private void getScoreFirebase() {
        final Dialog pDialog = new Dialog(mContext, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
            pDialog.show();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference leaderboard_ref = database.getReference("leaderboard");
        final DatabaseReference students_ref = leaderboard_ref.child("student_id");
        // mDatabase.child("users").setValue("sumit");
         mPref=SharedPreferences.getInstance(mContext);
        my_student_id=mPref.getLoggedUserData(GlobalConstant.getInstance().STUDENT_ID);
        final String input = "s";
        Query citiesQuery = students_ref.orderByKey().startAt(input).endAt(input + "\uf8ff");
        citiesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pDialog.dismiss();
                final ArrayList<Firebase_Pojo> mList = new ArrayList<>();
                final int[] my_total_score = {0};
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final DatabaseReference quizes_ref = students_ref.child(postSnapshot.getKey());
                    final String input = "q";
                    Query citiesQuery = quizes_ref.orderByKey().startAt(input).endAt(input + "\uf8ff");
                    citiesQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<String> students = new ArrayList<String>();
                           // AppLog.getInstance().printLog(mContext, "on data changed called ");
                          //  AppLog.getInstance().printLog(mContext, "DataSnapshot::::" + dataSnapshot);
                            Firebase_Pojo mPojo = null;
                            int total_score = 0;

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                students.add(postSnapshot.getValue().toString());
                                 mPojo = postSnapshot.getValue(Firebase_Pojo.class);
                                    total_score += Integer.parseInt(mPojo.quiz_score);
                                    if(mPojo.student_id.equalsIgnoreCase(my_student_id))
                                        my_total_score[0] += Integer.parseInt(mPojo.quiz_score);
                            }
                            mPojo.total_score=total_score;
                            mList.add(mPojo);
                               AppLog.getInstance().printLog(mContext,"inner method called");
                            layoutManager = new LinearLayoutManager(mContext);
                            mBinding.rvLeaderboard.setLayoutManager(layoutManager);
                            LeaderBoardAdapter mAdapter = new LeaderBoardAdapter(mContext,  sortListPostorder(mList, my_total_score[0]), mActivity, R.layout.leaderboard_row_layout, GlobalConstant.getInstance().TYPE_LEADERBOARD,my_student_id,my_total_score[0]);
                            mBinding.rvLeaderboard.setAdapter(mAdapter);
                            mBinding.rvLeaderboard.addOnItemTouchListener(
                                    new RecyclerItemClickListener(getActivity(), new   RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            // TODO Handle item click
                                            ///Other user total-score
                                            isToastRefresh=true;
                                           /* if(isToastRefresh) {
                                                if (mList.get(position).student_id.equalsIgnoreCase(my_student_id)) {
                                                    AppLog.getInstance().printToast(mContext, "Your Score is: " + my_total_score[0]);
                                                } else if (mList.get(position).total_score == my_total_score[0]) {
                                                    AppLog.getInstance().printToast(mContext, "Your and " + mList.get(position).student_name + " both are at same level");
                                                } else if (mList.get(position).total_score > my_total_score[0]) {
                                                    AppLog.getInstance().printToast(mContext, "Your need " + ((mList.get(position).total_score) - my_total_score[0]) + " points to beat " + mList.get(position).student_name);
                                                } else if (mList.get(position).total_score < my_total_score[0]) {
                                                    AppLog.getInstance().printToast(mContext, "Your are " + (my_total_score[0] - (mList.get(position).total_score)) + " points ahead from " + mList.get(position).student_name);
                                                }
                                                isToastRefresh=false;
                                            }*/
                                        }
                                    })
                            );

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            pDialog.dismiss();
                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pDialog.dismiss();
            }
        });
    }

    private ArrayList<Firebase_Pojo> sortListPostorder(ArrayList<Firebase_Pojo> mList, int my_total_score) {
        Firebase_Pojo temp;
        /**
         * Sort the arraylist in postorder according to the "total_score"
         */
        for(int i=0;i< mList.size();i++){
            for(int j=i+1;j< mList.size();j++){
                int first_index=mList.get(i).total_score;
                int second_index=mList.get(j).total_score;
                if(first_index<second_index){
                    temp=mList.get(i);
                    mList.set(i,mList.get(j));
                    mList.set(j,temp);
                }
            }
        }

        //Update Total Need Score.
        if(mList.get(0).student_id.equalsIgnoreCase(my_student_id))
             mBinding.moreScore.setText(R.string.congratulations_to_top_scorer);
        else if(mList.get(0).total_score==my_total_score)
             mBinding.moreScore.setText(R.string.congratulations_to_top_scorer);
        else
            mBinding.moreScore.setText(Html.fromHtml("You Need " + "<font color=\"#ff8800\">"+(mList.get(0).total_score-my_total_score)+"</font>"+" more score to become Top Scorer."));


        return mList;

    }

}


/**
 * Developed by AbhiAndroid.com
 */