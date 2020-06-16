package com.example.popularmovies1;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mPosters;
    private String[] paths;

    String url = "http://image.tmdb.org/t/p/w185";
    String TAG = "ImageAdapter";

    public ImageAdapter(Context context, ArrayList<String> posters) {
        mContext = context;
        mPosters = posters;

        try {
            paths = new String[mPosters.size()];
            for (int i = 0; i < mPosters.size(); i++) {
                paths[i] = url + mPosters.get(i);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Error!", e);
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(10, 10, 10, 10);
        } else
            imageView = (ImageView) view;

        Picasso.get().load(paths[i]).into(imageView);
        return imageView;
    }
}
