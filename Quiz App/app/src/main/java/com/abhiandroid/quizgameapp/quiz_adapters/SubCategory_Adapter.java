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
import com.abhiandroid.quizgameapp.activity.QuizActivity;
import com.abhiandroid.quizgameapp.activity.SubcategoryActivity;
import com.abhiandroid.quizgameapp.constant.GlobalConstant;
import com.abhiandroid.quizgameapp.model.Category_Pojo;
import com.abhiandroid.quizgameapp.model.SubCategory_Pojo;
import com.abhiandroid.quizgameapp.utils.AppLog;
import com.abhiandroid.quizgameapp.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import id.zelory.compressor.Compressor;


/**
 * Developed by AbhiAndroid.com
 */

public class SubCategory_Adapter extends RecyclerView.Adapter<SubCategory_Adapter.ViewHolder> {
    private SubCategory_Pojo subcategory;
    private Context mContext;
    private List<SubCategory_Pojo.Data> data;
    Activity mActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
      //  public TextView txtFooter;
        public ImageView icon;
        public View layout;


        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
           // txtFooter = (TextView) v.findViewById(R.id.secondLine);
            icon = (ImageView) v.findViewById(R.id.icon);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.getInstance().setRippleEffect(v);
                    Bundle bundle=new Bundle();
                    bundle.putString("subcat_id",data.get(getPosition()).subcat_id);
                    bundle.putString("intent_from",  GlobalConstant.getInstance().TYPE_OTHER_SCREEN);
                    mActivity.startActivity(new Intent(mContext,QuizActivity.class).putExtra("bundle",bundle));

                }
            });
        }
    }

    /*public void add(int position, String item) {
        category.data.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }*/

    // Provide a suitable constructor (depends on the kind of dataset)
    public SubCategory_Adapter(Context mContext, Activity mActivity, SubCategory_Pojo subcategory, List<SubCategory_Pojo.Data> data) {
        this.subcategory = subcategory;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.data=data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SubCategory_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.subcategory_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.txtHeader.setText(data.get(position).subcat_name);

        AppLog.getInstance().printLog(mContext,data.get(position).subcat_name);
     //   holder.txtFooter.setText(category.data.get(position).id);
        File file=new File(data.get(position).subcat_image);
        try {
            File compressedImageFile = new Compressor(mContext).compressToFile(file);
            Picasso.with(mContext).load(compressedImageFile).fit().placeholder(R.drawable.image_placeholder).into(holder.icon);

        } catch (Exception e) {
            e.printStackTrace();
        }
        AppLog.getInstance().printLog(mContext,data.get(position).subcat_image);
        Picasso.with(mContext).load(data.get(position).subcat_image).fit().placeholder(R.drawable.image_placeholder).into(holder.icon);
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