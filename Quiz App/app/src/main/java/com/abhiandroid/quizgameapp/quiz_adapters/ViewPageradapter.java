package com.abhiandroid.quizgameapp.quiz_adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.fragments.LeaderBoard_Frag;
import com.abhiandroid.quizgameapp.fragments.MyQuizs_Frag;

/**
 * Developed by AbhiAndroid.com
 */

public class ViewPageradapter extends FragmentPagerAdapter {

    private Context mContext;

    public ViewPageradapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LeaderBoard_Frag();
        } else{
            return new MyQuizs_Frag();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.leaderboard);
            case 1:
                return mContext.getString(R.string.my_quizes);
            default:
                return null;
        }
    }

}


/**
 * Developed by AbhiAndroid.com
 */