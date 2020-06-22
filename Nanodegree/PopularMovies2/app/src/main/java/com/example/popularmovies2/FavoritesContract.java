package com.example.popularmovies2;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {
    public static final String AUTHORITY = "com.example.popularmovies2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class Favorite implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_OVERVIEW = "movie_overview";
        public static final String COLUMN_POSTER_URL = "movie_poster_url";
        public static final String COLUMN_USER_RATING = "movie_user_rating";
        public static final String COLUMN_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_ID = "movie_id";
    }
}