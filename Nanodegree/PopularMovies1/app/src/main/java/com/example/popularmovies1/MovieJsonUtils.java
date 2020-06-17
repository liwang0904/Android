package com.example.popularmovies1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MovieJsonUtils {
    private final static String MOVIE_DB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/";
    private final static String SIZE = "w500";

    public static ArrayList<Movie> getMoviesFromJson(String movieJsonString) throws JSONException {
        final String ID = "id";
        final String TITLE = "title";
        final String POPULARITY = "popularity";
        final String RATING = "vote_average";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String SYNOPSIS = "overview";

        ArrayList<Movie> movies = new ArrayList<Movie>() {};

        JSONObject movieJson = new JSONObject(movieJsonString);
        JSONArray results = movieJson.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            Movie movie = new Movie();

            JSONObject j = results.getJSONObject(i);

            movie.setId(j.getInt(ID));
            movie.setTitle(j.getString(TITLE));
            movie.setPopularity(j.getInt(POPULARITY));
            movie.setRating(j.getInt(RATING));
            movie.setPosterPath(MOVIE_DB_POSTER_BASE_URL + SIZE + j.getString(POSTER_PATH));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            try {
                movie.setReleaseDate(formatter.parse(j.getString(RELEASE_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            movie.setOverview(j.getString(SYNOPSIS));
            movies.add(movie);
        }
        return movies;
    }
}
