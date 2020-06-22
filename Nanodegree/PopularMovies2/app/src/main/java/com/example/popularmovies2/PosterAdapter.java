package com.example.popularmovies2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {
    public static final String BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE = "w185/";

    final private PosterAdapterOnClickHandler mClickHandler;

    private Movie[] movies;

    public PosterAdapter(PosterAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface PosterAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public class PosterAdapterViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public final ImageView poster;

        public PosterAdapterViewHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Movie movie = movies[getAdapterPosition()];
            mClickHandler.onClick(movie);
        }
    }

    @NonNull
    @Override
    public PosterAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_poster, parent, false);
        return new PosterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterAdapterViewHolder holder, int position) {
        String url = BASE_URL + POSTER_SIZE + movies[position].getPosterUrl();
        Picasso.get().load(url).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.length;
    }

    public void setPosterData(Movie[] moviesList) {
        movies = moviesList;
        notifyDataSetChanged();
    }
}