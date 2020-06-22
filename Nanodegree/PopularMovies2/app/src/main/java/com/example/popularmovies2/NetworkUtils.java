package com.example.popularmovies2;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    final static String PARAM_API_KEY = "api_key";
    final static String PARAM_APPEND_TO_RESPONSE = "append_to_response";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

    public static URL buildUrl(String sortOrder, String apiKey) {
        Uri builtUri = Uri.parse(BASE_URL + sortOrder).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTrailersAndReviewsUrl(String movieID, String apiKey) {
        Uri builtUri = Uri.parse(BASE_URL + movieID).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_APPEND_TO_RESPONSE, "reviews,videos")
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
