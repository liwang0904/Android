package com.abhiandroid.quizgameapp.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.ConnectivityManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.activity.CategoryActivity;
import com.abhiandroid.quizgameapp.activity.PlayQuizActivity;
import com.abhiandroid.quizgameapp.activity.QuizActivity;
import com.abhiandroid.quizgameapp.activity.Quiz_Search_Activity;
import com.abhiandroid.quizgameapp.activity.Search_Activity;
import com.abhiandroid.quizgameapp.activity.ShowScoreActivity;
import com.abhiandroid.quizgameapp.activity.StartQuizActivity;
import com.abhiandroid.quizgameapp.activity.SubcategoryActivity;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.quiz_preferences.SharedPreferences;

import java.util.Random;

/**
 * Developed by AbhiAndroid.com
 */

public class CommonUtils {
    /**
     *
     * @return current class instance
     */
    public static CommonUtils getInstance(){
        return new CommonUtils();
    }

    /**
     * @SET Ripple effect on imageview
     * @param view
     */
    public void setRippleEffect(ImageView view){
         RippleDrawable rippleDrawable;
        Drawable drawable=view.getDrawable();
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] {
                Color.RED
        };
        ColorStateList myList = new ColorStateList(states, colors);
        rippleDrawable=new RippleDrawable(myList,drawable,null);
        //  rippleDrawable.setHotspot(event.getX(), event.getY());
        rippleDrawable.setColor(ColorStateList.valueOf(Color.parseColor("#4DFFFFFF")));
        view.setImageDrawable(rippleDrawable);
    }
    /**
     * @SET Ripple effect on view
     * @param view
     */
    public void setRippleEffect(View view){
        RippleDrawable rippleDrawable;
        Drawable drawable=view.getBackground();
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] {
                Color.RED
        };
        ColorStateList myList = new ColorStateList(states, colors);
        rippleDrawable=new RippleDrawable(myList,drawable,null);
        //  rippleDrawable.setHotspot(event.getX(), event.getY());
        rippleDrawable.setColor(ColorStateList.valueOf(Color.parseColor("#4DFFFFFF")));
        view.setBackground(rippleDrawable);
    }
    /**
     * Check Quiz logged-in or not
     */
    public boolean isUserLoggedIn(Context context){
        String social_id=SharedPreferences.getInstance(context).getLoggedUserData(GlobalConstant.getInstance().SOCIAL_ID);
        return social_id==null ? false : true;
    }
    public int getRandomOption(int total_no){
        Random rand = new Random();

        int  n = rand.nextInt(total_no) + 1;
       return n;

    }

    /**
     * Check internet connection.
     * @param context
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void clearStack() {
        if(CategoryActivity.mActivity!=null)
            CategoryActivity.mActivity.finish();

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




    }
}


/**
 * Developed by AbhiAndroid.com
 */