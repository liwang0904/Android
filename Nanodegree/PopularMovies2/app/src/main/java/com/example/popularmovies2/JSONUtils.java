package com.example.popularmovies2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
    public static Movie[] getPosters(String JSONResponse) throws JSONException {
        JSONObject JSONObject = new JSONObject(JSONResponse);
        JSONArray results = JSONObject.getJSONArray("results");
        Movie[] movies = new Movie[results.length()];
        for (int i = 0; i < results.length(); i++) {
            Movie movie = new Movie();
            JSONObject result = results.getJSONObject(i);
            movie.setTitle(result.getString("title"));
            movie.setOverview(result.getString("overview"));
            movie.setPosterUrl(result.getString("poster_path"));
            movie.setReleaseDate(result.getString("release_date"));
            movie.setUserRating(String.valueOf(result.getDouble("vote_average")));
            movie.setId(String.valueOf((int) result.getDouble("id")));
            movies[i] = movie;
        }
        return movies;
    }

    public static Review[] getReviews(String JSONResponse) throws JSONException {
        JSONObject movieJson = new JSONObject(JSONResponse);
        JSONObject reviewJson = movieJson.getJSONObject("reviews");
        JSONArray results = reviewJson.getJSONArray("results");
        Review[] reviews = new Review[results.length()];
        for (int i = 0; i < results.length(); i++) {
            Review review = new Review();
            JSONObject result = results.getJSONObject(i);
            review.setAuthor(result.getString("author"));
            review.setContent(result.getString("content"));
            reviews[i] = review;
        }
        return reviews;
    }

    public static Trailer[] getTrailers(String JSONResponse) throws JSONException {
        JSONObject movieJson = new JSONObject(JSONResponse);
        JSONObject trailerJson = movieJson.getJSONObject("videos");
        JSONArray results = trailerJson.getJSONArray("results");
        Trailer[] trailers = new Trailer[results.length()];
        for (int i = 0; i < results.length(); i++) {
            Trailer trailer = new Trailer();
            JSONObject result = results.getJSONObject(i);
            trailer.setName(result.getString("name"));
            trailer.setSite(result.getString("site"));
            trailer.setKey(result.getString("key"));
            trailers[i] = trailer;
        }
        return trailers;
    }
}
