package com.example.popularmovies1;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private final static String API_KEY = "put_your_api_key_here";
    private final static String POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private final static String TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";

    private final static String PARAM_API_KEY = "api_key";

    public static URL buildUrl(int typeOfMovies) {
        Uri uri = null;
        if (typeOfMovies == 1) {
            uri = Uri.parse(POPULAR_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_API_KEY, API_KEY)
                    .build();
        } else if (typeOfMovies == 2) {
            uri = Uri.parse(TOP_RATED_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_API_KEY, API_KEY)
                    .build();
        }

        URL url = null;
        try {
            if (uri != null) {
                url = new URL(uri.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponse(URL url) throws IOException {
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
