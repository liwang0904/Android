package com.abhiandroid.quizgameapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.activity.StartQuizActivity;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.databinding.FragmentLeaderBoardBinding;
import com.abhiandroid.quizgameapp.databinding.FragmentMyQuizsBinding;
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
import java.util.List;


/**
 * Developed by AbhiAndroid.com
 */

public class MyQuizs_Frag extends Fragment {
    static int current_page=0,total_pages=0;
    private final int SHOW_PROGRESS_DIALOG=1;
    private final int HIDE_PROGRESS_DIALOG=0;
    private FragmentMyQuizsBinding mBinding;

    private LinearLayoutManager layoutManager;
    private Context mContext;
    private Activity mActivity;

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_quizs_, container, false);
        return mBinding.getRoot();

    }
    @Override
    public void onResume() {
        super.onResume();
        current_page=total_pages=0;
        mBinding.llLoadMore.setVisibility(View.GONE);
        getMyQuizes();

    }
    public void loadMore(View viewGroup){
        mBinding.llLoadMore.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        getMyQuizes();
    }

    private void getMyQuizes() {
        final Dialog pDialog = new Dialog(mContext, R.style.NewDialog);
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference leaderboard_ref = database.getReference("leaderboard");
        final DatabaseReference students_ref = leaderboard_ref.child("student_id");
        // mDatabase.child("users").setValue("sumit");
        SharedPreferences mPref=SharedPreferences.getInstance(mContext);
        final String student_id=mPref.getLoggedUserData(GlobalConstant.getInstance().STUDENT_ID);

        final DatabaseReference quizes_ref = students_ref.child(student_id);
        final String input = "q";
        Query citiesQuery = quizes_ref.orderByKey().startAt(input).endAt(input + "\uf8ff");
        citiesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pDialog.dismiss();
                List<String> students = new ArrayList<String>();
                AppLog.getInstance().printLog(mContext, "on data changed called ");
                AppLog.getInstance().printLog(mContext, "DataSnapshot::::" + dataSnapshot);
                final ArrayList<Firebase_Pojo> mList=new ArrayList<>();
                int total_score = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    students.add(postSnapshot.getValue().toString());
                    Firebase_Pojo mPojo=  postSnapshot.getValue(Firebase_Pojo.class);
                    mList.add(mPojo);
                    total_score += Integer.parseInt(mPojo.quiz_score);

                }
                layoutManager = new LinearLayoutManager(mContext);
                mBinding.moreScore.setText(" "+total_score);
                mBinding.rvMyquizes.setLayoutManager(layoutManager);
                LeaderBoardAdapter mAdapter=new LeaderBoardAdapter(mContext,mList,mActivity,R.layout.category_row_layout,GlobalConstant.getInstance().TYPE_MYQUIZ);
                mBinding.rvMyquizes.setAdapter(mAdapter);
                /////////////

                mBinding.rvMyquizes.addOnItemTouchListener(
                        new RecyclerItemClickListener(getActivity(), new   RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                // TODO Handle item click
                                Bundle bundle=new Bundle();
                                bundle.putString("quiz_id",mList.get(position).quiz_id);
                                bundle.putString("intent_from",  GlobalConstant.getInstance().TYPE_OTHER_SCREEN);
                                mContext.startActivity(new Intent(mActivity, StartQuizActivity.class).putExtra("bundle",bundle));
                            }
                        })
                );
                AppLog.getInstance().printSnackbar(mContext,"Quiz App Leaderboard");

                ///////////////////////////
                if (dataSnapshot.getValue() == null) {
                    AppLog.getInstance().printLog(mContext, "Dtaa base nullll");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pDialog.dismiss();
            }
        });
    }
}


/**
 * Developed by AbhiAndroid.com
 */