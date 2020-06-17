package com.example.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final ArrayList<Movie> mMovies;
    private final Context context;

    MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        mMovies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        final String posterPath = mMovies.get(position).getPosterPath();
        Picasso.get().setLoggingEnabled(true);
        Picasso.get()
                .load(posterPath)
                .into(holder.posterView);

        holder.posterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("movie", mMovies.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final ImageView posterView;

        MovieViewHolder(View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.movie_image);
        }
    }
}
