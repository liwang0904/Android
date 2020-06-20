package com.example.popularmovies2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CustomReviewAdapter extends ArrayAdapter<Review> {
    private int layoutResourceId;
    private ArrayList<Review> reviews;

    private String TAG = MainActivity.class.getSimpleName();

    public CustomReviewAdapter(Context context, int layoutResourceId, ArrayList<Review> reviews) {
        super(context, layoutResourceId, reviews);
        this.layoutResourceId = layoutResourceId;
        this.reviews = reviews;

        Log.i(TAG, "reviewsNumber: " + reviews.size());
    }

    @Override
    public int getCount() {
        return reviews == null ? 0 : reviews.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ReviewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ReviewHolder();
            holder.textAuthor = (TextView) row.findViewById(R.id.tv_review_author);
            row.setTag(holder);
        } else {
            holder = (ReviewHolder) row.getTag();
        }
        Review review = getItem(position);
        holder.textAuthor.setText(review.getAuthor());
        return row;
    }

    private static class ReviewHolder {
        TextView textAuthor;
    }
}
