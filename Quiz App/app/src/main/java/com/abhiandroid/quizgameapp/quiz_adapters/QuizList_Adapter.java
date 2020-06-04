package com.abhiandroid.quizgameapp.quiz_adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.activity.StartQuizActivity;
import com.abhiandroid.quizgameapp.activity.SubcategoryActivity;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.fragments.LoginDialogfragment;
import com.abhiandroid.quizgameapp.interfaces.OnLoginCallbackListener;
import com.abhiandroid.quizgameapp.model.QuizList_Pojo;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import id.zelory.compressor.Compressor;


/**
 * Developed by AbhiAndroid.com
 */

public class QuizList_Adapter extends RecyclerView.Adapter<QuizList_Adapter.ViewHolder> {
    private QuizList_Pojo category;
    private Context mContext;
    List<QuizList_Pojo.Data> data;
    Activity mActivity;



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
                    if(CommonUtils.getInstance().isUserLoggedIn(mContext)) {
                        startQuiz(getPosition());
                    }
                    else{

                        LoginDialogfragment newFragment =LoginDialogfragment.getInstance(true,mContext,mActivity, new OnLoginCallbackListener() {
                            @Override
                            public void OnCancel() {

                            }

                            @Override
                            public void onSuccess() {
                                startQuiz(getPosition());
                            }
                        });
                        Bundle bundle=new Bundle();
                        bundle.putString("message",mActivity.getResources().getString(R.string.login_mandotry_play_quiz));
                        newFragment.setArguments(bundle);
                        newFragment.show(mActivity.getFragmentManager(), "dialog");
                    }

                }
            });
        }
    }

    private void startQuiz(int pos) {
        Bundle bundle=new Bundle();
        bundle.putString("quiz_id",data.get(pos).quiz_id);
        bundle.putString("intent_from",  GlobalConstant.getInstance().TYPE_OTHER_SCREEN);
        mActivity.startActivity(new Intent(mActivity, StartQuizActivity.class).putExtra("bundle",bundle));
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public QuizList_Adapter(Context mContext, QuizList_Pojo category, List<QuizList_Pojo.Data> data, Activity mActivity) {
        this.category = category;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.data=data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public QuizList_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.category_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RelativeLayout relative_layout=v.findViewById(R.id.relative_layout);
        CommonUtils.getInstance().setRippleEffect(relative_layout);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.txtHeader.setText(data.get(position).quiz_name);

        AppLog.getInstance().printLog(mContext,data.get(position).quiz_name);


        holder.txtFooter.setText(Html.fromHtml("Best Score : " + "<font color=\"#ff8800\">" + category.data.get(position).best_score + "</font>"
               ));
       // holder.txtFooter.setText("Best Score : "+category.data.get(position).best_score);
        File file=new File(data.get(position).quiz_image);
        try {
            File compressedImageFile = new Compressor(mContext).compressToFile(file);
            Picasso.with(mContext).load(compressedImageFile).fit().placeholder(R.drawable.image_placeholder).into(holder.icon);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Picasso.with(mContext).load(data.get(position).quiz_image).fit().placeholder(R.drawable.image_placeholder).into(holder.icon);

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