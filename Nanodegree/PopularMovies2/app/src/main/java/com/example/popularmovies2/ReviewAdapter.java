package com.example.popularmovies2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private Review[] reviews;

    public ReviewAdapter() {
    }

    public static class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvAuthor;
        public final TextView tvContent;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int id = R.layout.item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(id, viewGroup, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        holder.tvAuthor.setText(reviews[position].getAuthor());
        holder.tvContent.setText(reviews[position].getContent());
    }

    public void setReviewData(Review[] reviewList) {
        reviews = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        return reviews.length;
    }
}