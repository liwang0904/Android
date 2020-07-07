package com.example.teleprompter;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    public static final String AUTHORITY = "com.example.teleprompter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "documents";


    public static final class Entry implements BaseColumns {
        public static final String TABLE_NAME = "documents";
        public static final String COLUMN_CLOUD_ID = "cloud_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_IS_TUTORIAL = "is_tutorial";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static Uri getUriForId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }
    }
}