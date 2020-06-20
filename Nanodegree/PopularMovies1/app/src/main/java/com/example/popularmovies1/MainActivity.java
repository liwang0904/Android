package com.example.popularmovies1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> mMovies;
    private int typeOfMovies = 1;
//    NoInternetDialog noInternetDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        final Dialog dialog = new Dialog(context);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            dialog.setContentView(R.layout.alert_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

            Button button = dialog.findViewById(R.id.button_retry);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                        recreate();
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
//        noInternetDialog = new NoInternetDialog.Builder(this).build();
            setContentView(R.layout.activity_main);
            startAsyncTask(typeOfMovies);
        }
    }

    private void startAsyncTask(int typeOfMovies) {
        URL url = NetworkUtils.buildUrl(typeOfMovies);
        new MovieApiPosterTask().execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_popular) {
            typeOfMovies = 1;
        } else if (item.getItemId() == R.id.sort_top_rated) {
            typeOfMovies = 2;
        }

        startAsyncTask(typeOfMovies);
        setUpRecyclerView();
        return true;
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        MovieAdapter movieAdapter = new MovieAdapter(this, mMovies);
        recyclerView.setAdapter(movieAdapter);
    }

    class MovieApiPosterTask extends AsyncTask<URL, ArrayList<Movie>, ArrayList<Movie>> {
        MovieApiPosterTask() {}

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            URL url = urls[0];
            String movieResults = null;
            ArrayList<Movie> movies = null;

            try {
                movieResults = NetworkUtils.getResponse(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                movies = MovieJsonUtils.getMoviesFromJson(movieResults);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mMovies = movies;
            setUpRecyclerView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        noInternetDialog.onDestroy();
    }
}