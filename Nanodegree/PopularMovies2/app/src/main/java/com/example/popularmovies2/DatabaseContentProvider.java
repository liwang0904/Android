package com.example.popularmovies2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.popularmovies2.FavoritesContract.Favorite.TABLE_NAME;

public class DatabaseContentProvider extends ContentProvider {
    private DatabaseHelper mDatabaseHelper;
    private static final UriMatcher uriMatcher = uriMatcher();

    public static UriMatcher uriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, 100);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        final SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        Cursor cursor = null;
        if (match == 100) {
            cursor = database.query(TABLE_NAME, strings, s, strings1, null, null, s1);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri uri1 = null;
        if (match == 100) {
            long id = database.insert(TABLE_NAME, null, contentValues);
            if (id > 0) {
                uri1 = ContentUris.withAppendedId(FavoritesContract.Favorite.CONTENT_URI, id);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri1;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int deleted = 0;
        if (match == 100) {
            deleted = database.delete(TABLE_NAME, s, strings);
        }
        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}