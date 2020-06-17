package com.example.popularmovies1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        movie = bundle.getParcelable("movie");
        setViews();
    }

    private void setViews() {
        TextView title = findViewById(R.id.title);
        title.setText(movie.getTitle());

        TextView releaseDate = findViewById(R.id.release_date);
        String pattern = "yyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        String dateString = dateFormat.format(movie.getReleaseDate());
        releaseDate.setText(dateString);

        ImageView poster = findViewById(R.id.poster);
        Picasso.get().load(movie.getPosterPath()).into(poster);

        TextView rating = findViewById(R.id.rating);
        rating.setText(String.format("%s%s", "Rating: ", movie.getRating()));

        TextView synopsis = findViewById(R.id.synopsis);
        synopsis.setText(movie.getOverview());
    }
}