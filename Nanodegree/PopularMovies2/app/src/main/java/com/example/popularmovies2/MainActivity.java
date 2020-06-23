package com.example.popularmovies2;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.Database.MovieViewModel;
import com.example.popularmovies2.databinding.ActivityMainBinding;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PosterAdapter.PosterAdapterOnClickHandler {
    private final String API_KEY = "put_your_api_key_here";

    private static final String SORT_POPULAR = "popular";
    private static final String SORT_TOP_RATED = "top_rated";
    public static final String SORT_FAVORITE = "favorites";
    private String sortType;

    ActivityMainBinding mBinding;

    private RecyclerView mRecyclerView;
    private TextView tvErrorMessage;
    private ProgressBar pbLoading;

    private PosterAdapter mPosterAdapter;

    private Movie[] movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mRecyclerView = mBinding.rvPosters;
        tvErrorMessage = mBinding.tvErrorMessage;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mPosterAdapter = new PosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        pbLoading = mBinding.pbLoading;
        sortType = SORT_POPULAR;
        if (savedInstanceState == null) {
            loadPosters(sortType);
        } else {
            movies = (Movie[]) savedInstanceState.getParcelableArray("movies");
            mPosterAdapter.setPosterData(Arrays.asList(movies));
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie", movie);
        intent.putExtra("base_url", PosterAdapter.BASE_URL);
        intent.putExtra("poster_size", PosterAdapter.POSTER_SIZE);
        intent.putExtra("api_key", API_KEY);
        intent.putExtra("sort_type", sortType);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_popular) {
            sortType = SORT_POPULAR;
        } else if (item.getItemId() == R.id.sort_top_rated) {
            sortType = SORT_TOP_RATED;
        } else if (item.getItemId() == R.id.sort_favorite) {
            sortType = SORT_FAVORITE;
        }
        loadPosters(sortType);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("movies", movies);
    }

    private void loadPosters(String sortType) {
        tvErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (sortType.equals(SORT_FAVORITE)) {
            getFavoritePosters();
        } else {
            new GetPosters().execute(sortType);
        }
    }

    public void getFavoritePosters() {
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getFavoriteMovieList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> favoriteMovieList) {
                movies = favoriteMovieList.toArray(new Movie[0]);
                mPosterAdapter.notifyDataSetChanged();
                mPosterAdapter.setPosterData(favoriteMovieList);
            }
        });
    }

    public class GetPosters extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... strings) {
            String sortType = strings[0];
            URL posterUrl = NetworkUtils.buildUrl(sortType, API_KEY);
            try {
                String JSONResponse = NetworkUtils.getResponseFromHttpUrl(posterUrl);
                return JSONUtils.getPosters(JSONResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] moviesList) {
            pbLoading.setVisibility(View.INVISIBLE);
            if (moviesList != null) {
                movies = moviesList;
                tvErrorMessage.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mPosterAdapter.setPosterData(Arrays.asList(movies));
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
                tvErrorMessage.setText("Please check your internet connection and try again.");
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}