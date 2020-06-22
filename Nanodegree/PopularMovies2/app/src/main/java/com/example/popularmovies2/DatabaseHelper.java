package com.example.popularmovies2;

import com.example.popularmovies2.FavoritesContract.Favorite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "favorites.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + Favorite.TABLE_NAME + " (" +
                Favorite._ID + " INTEGER PRIMARY KEY, " +
                Favorite.COLUMN_TITLE + " TEXT NOT NULL, " +
                Favorite.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                Favorite.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                Favorite.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                Favorite.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                Favorite.COLUMN_ID + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Favorite.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}