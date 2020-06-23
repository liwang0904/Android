package com.example.popularmovies2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies2.Database.MovieRoomDatabase;
import com.example.popularmovies2.Database.MovieViewModel;
import com.example.popularmovies2.databinding.ActivityDetailBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {
    ActivityDetailBinding mBinding;

    private TrailerAdapter mTrailerAdapter;

    private ReviewAdapter mReviewAdapter;

    private ProgressBar pbLoading;
    private FloatingActionButton favoriteButton;
    private boolean favorite = false;

    private MovieRoomDatabase database;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        TextView tvTitle = mBinding.tvTitle;
        TextView tvRating = mBinding.tvRating;
        ImageView ivPoster = mBinding.ivPoster;
        TextView tvOverview = mBinding.tvOverview;
        pbLoading = mBinding.pbLoading;
        favoriteButton = mBinding.favoriteButton;

        RecyclerView mTrailerRecyclerView = mBinding.rvTrailers;
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        RecyclerView mReviewRecyclerView = mBinding.rvReviews;
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        database = MovieRoomDatabase.getDatabase(getApplicationContext());

        final MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra("movie")) {
            movie = intent.getParcelableExtra("movie");
            String baseUrl = intent.getStringExtra("base_url");
            String posterSize = intent.getStringExtra("poster_size");
            String sortType = intent.getStringExtra("sort_type");
            if (sortType.equals(MainActivity.SORT_FAVORITE) || movie.isFavorite()) {
                favorite = true;
            }
            setFavoriteButton(favorite);

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (favorite) {
                        movieViewModel.delete(movie);
                        Toast.makeText(DetailActivity.this, movie.getTitle() + " was removed to you favorites.", Toast.LENGTH_SHORT).show();
                    } else {
                        movieViewModel.insert(movie);
                        Toast.makeText(DetailActivity.this, movie.getTitle() + " was added to you favorites.", Toast.LENGTH_SHORT).show();
                    }
                    favorite = !favorite;
                    movie.setFavorite(favorite);
                    setFavoriteButton(favorite);
                }
            });

            String apiKey = intent.getStringExtra("api_key");
            String titleAndYear = String.format(getString(R.string.title_and_year), movie.getTitle(), movie.getReleaseDate().substring(0, 4));
            tvTitle.setText(titleAndYear);
            String rating = String.format(getString(R.string.rating), movie.getUserRating());
            tvRating.setText(rating);
            String id = movie.getId();

            Picasso.get().load(baseUrl + posterSize + movie.getPosterUrl()).into(ivPoster);
            tvOverview.setText(movie.getOverview());
            new GetTrailersAndReviewsTask().execute(id, apiKey, null);
        }
    }

    @Override
    public void onClick(Trailer trailer) {
        Uri trailerUri = Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey());
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void setFavoriteButton(final boolean favorite) {
        DetailViewModelFactory viewModelFactory = new DetailViewModelFactory(database, movie.getId());
        DetailViewModel detailViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel.class);
        detailViewModel.checkIfMovieInDb().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(Movie movieInDb) {
                if (movieInDb == null) {
                    favoriteButton.setImageResource(R.drawable.ic_unfavorite);
                } else if (movie.getId().equals(movieInDb.getId())) {
                    favoriteButton.setImageResource(R.drawable.ic_favorite);
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_unfavorite);
                }
            }
        });
    }

    public class GetTrailersAndReviewsTask extends AsyncTask<String, String, List<Object>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Object> doInBackground(String... strings) {
            URL url = NetworkUtils.buildTrailersAndReviewsUrl(strings[0], strings[1]);
            try {
                String JSONResponse = NetworkUtils.getResponseFromHttpUrl(url);
                Review[] reviews = JSONUtils.getReviews(JSONResponse);
                Trailer[] trailers = JSONUtils.getTrailers(JSONResponse);
                List<Object> reviewsAndTrailers = new ArrayList<>();
                reviewsAndTrailers.add(reviews);
                reviewsAndTrailers.add(trailers);
                return reviewsAndTrailers;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Object> objects) {
            if (objects != null) {
                Review[] reviews = (Review[]) objects.get(0);
                mReviewAdapter.setReviewData(reviews);
                Trailer[] trailers = (Trailer[]) objects.get(1);
                mTrailerAdapter.setTrailerData(trailers);
            } else {
                Toast.makeText(DetailActivity.this, "Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
            }
            pbLoading.setVisibility(View.INVISIBLE);
        }
    }
}