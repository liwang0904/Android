package com.example.popularmovies2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    final private TrailerAdapterOnClickHandler mClickHandler;
    private Trailer[] trailers;

    public TrailerAdapter(TrailerAdapterOnClickHandler onClickHandler) {
        mClickHandler = onClickHandler;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView ivTrailerThumbnail;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            ivTrailerThumbnail = view.findViewById(R.id.iv_trailer_thumbnail);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Trailer trailer = trailers[getAdapterPosition()];
            mClickHandler.onClick(trailer);
        }
    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int id = R.layout.item_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(id, viewGroup, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        String thumbnailUrl = "https://img.youtube.com/vi/" + trailers[position].getKey() + "/default.jpg";
        Picasso.get()
                .load(thumbnailUrl)
                .into(holder.ivTrailerThumbnail);
    }

    public void setTrailerData(Trailer[] trailerList) {
        trailers = trailerList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.length;
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer clickedItem);
    }
}
