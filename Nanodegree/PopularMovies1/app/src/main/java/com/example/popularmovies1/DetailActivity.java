package com.example.popularmovies1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    Integer id;
    TextView title, user_rating, release_date, synopsis;
    ImageView poster_image;

    String api_key = "33794c1f64d8154fab6ffab92de21f27";

    public class FetchMovieDetails extends AsyncTask<Void, Void, Void> {

        String original_title, date, plot_synopsis, poster_path;
        Double ratings;

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection;
            BufferedReader reader;
            String movieJsonString;

            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + id + "?api_key=" + api_key);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                if (builder.length() == 0) {
                    return null;
                }

                movieJsonString = builder.toString();

                JSONObject main = new JSONObject(movieJsonString);
                original_title = main.getString("original_title");
                date = main.getString("release_date");
                ratings = main.getDouble("vote_average");
                plot_synopsis = main.getString("overview");
                poster_path = "http://image.tmdb.org/t/p/w185" + main.getString("poster_path");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            title.setText(original_title);
            user_rating.setText("User Ratings: " + ratings);
            release_date.setText("Release Date: " + date);
            synopsis.setText(plot_synopsis);
            poster_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(poster_path).into(poster_image);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        id = intent.getIntExtra("Movie ID", 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.title);
        user_rating = findViewById(R.id.user_rating);
        release_date = findViewById(R.id.release_date);
        synopsis = findViewById(R.id.synopsis);
        poster_image = findViewById(R.id.poster_image);

        FetchMovieDetails fetchMovieDetails = new FetchMovieDetails();
        fetchMovieDetails.execute();
    }
}