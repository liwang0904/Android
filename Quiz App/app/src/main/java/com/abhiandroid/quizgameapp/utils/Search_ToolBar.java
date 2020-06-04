package com.abhiandroid.quizgameapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.activity.CategoryActivity;

/**
 * Developed by AbhiAndroid.com
 */

public class Search_ToolBar extends RelativeLayout{
    LayoutInflater mInflater;
  public   ImageView imageView,search_icon;
    TextView textView;
    View view;

    public Search_ToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=this.mInflater.inflate(R.layout.quiz_search_toolbar,this);
        imageView=view.findViewById(R.id.menu);
        search_icon=view.findViewById(R.id.search_icon);
        textView=view.findViewById(R.id.text);

    }


    public void setImage(Drawable drawable){
        imageView.setImageDrawable(drawable);
    }

    public void setText(String text){
       textView.setText(text);
    }


}

/**
 * Developed by AbhiAndroid.com
 */