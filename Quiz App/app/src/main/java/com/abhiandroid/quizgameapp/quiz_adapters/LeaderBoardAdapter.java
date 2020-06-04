package com.abhiandroid.quizgameapp.quiz_adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.activity.CategoryActivity;
import com.abhiandroid.quizgameapp.activity.StartQuizActivity;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.Firebase_Pojo;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import id.zelory.compressor.Compressor;


/**
 * Developed by AbhiAndroid.com
 */

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {
    private Category_Pojo category;
    private Context mContext;
    List<Firebase_Pojo> data;
    Activity mActivity;
    int category_row_layout;
    int view_type;
    String my_student_id;
    int my_total_score;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public ImageView icon;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            icon = (ImageView) v.findViewById(R.id.icon);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(view_type== GlobalConstant.getInstance().TYPE_LEADERBOARD)
                    {
                        if (data.get(getPosition()).student_id.equalsIgnoreCase(my_student_id)) {
                        AppLog.getInstance().printToast(mContext, "Your Score is: " + my_total_score);
                    } else if (data.get(getPosition()).total_score == my_total_score) {
                        AppLog.getInstance().printToast(mContext, "Your and " + data.get(getPosition()).student_name + " both are at same level");
                    } else if (data.get(getPosition()).total_score > my_total_score) {
                            AppLog.getInstance().printToast(mContext, "Your need " + ((data.get(getPosition()).total_score) - my_total_score) + " points to beat " + data.get(getPosition()).student_name);
                        } else if (data.get(getPosition()).total_score < my_total_score) {
                            AppLog.getInstance().printToast(mContext, "Your are " + (my_total_score - (data.get(getPosition()).total_score)) + " points ahead from " + data.get(getPosition()).student_name);
                        }
                    }
                }
            });
        }
    }


    // constructor for MY_Quizes Fragment
    public LeaderBoardAdapter(Context mContext, List<Firebase_Pojo> data, Activity mActivity, int category_row_layout,int view_type) {
        this.category = category;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.data=data;
        this.category_row_layout=category_row_layout;
        this.view_type=view_type;
    }
    // constructor for LEADERBOARD Fragment
    public LeaderBoardAdapter(Context mContext, List<Firebase_Pojo> data, Activity mActivity, int category_row_layout,int view_type,String my_student_id,int my_total_score) {
        this.category = category;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.data=data;
        this.category_row_layout=category_row_layout;
        this.view_type=view_type;
        this.my_student_id=my_student_id;
        this.my_total_score=my_total_score;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LeaderBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(category_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if(view_type== GlobalConstant.getInstance().TYPE_MYQUIZ) {
            holder.txtHeader.setText(data.get(position).quiz_name);
            holder.txtFooter.setText("Your Best Score : "+data.get(position).quiz_score);
            if(data.get(position).quiz_image!=null) {
            File file = new File(data.get(position).quiz_image);
                try {
                    File compressedImageFile = new Compressor(mContext).compressToFile(file);
                    Picasso.with(mContext).load(compressedImageFile).fit().placeholder(R.drawable.image_placeholder).into(holder.icon);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Picasso.with(mContext).load(data.get(position).quiz_image).fit().placeholder(R.drawable.image_placeholder).into(holder.icon);

          }
        else{
            AppLog.getInstance().printLog(mContext,"image::::"+data.get(position).student_image);
            holder.txtHeader.setText(data.get(position).student_name);
            holder.txtFooter.setText("Score : "+data.get(position).total_score);
            if(data.get(position).student_image!=null) {

                try {
                    File file = new File(data.get(position).student_image);
                    File compressedImageFile = new Compressor(mContext).compressToFile(file);
                    Picasso.with(mContext).load(compressedImageFile).fit().placeholder(R.drawable.image_placeholder).into(holder.icon);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Picasso.with(mContext).load(data.get(position).student_image).fit().placeholder(R.drawable.user_avatar).into(holder.icon);

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

}

/**
 * Developed by AbhiAndroid.com
 */
