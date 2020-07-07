package com.example.teleprompter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContentProvider extends android.content.ContentProvider {
    private Helper helper;
    private static UriMatcher matcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_FAVORITES, 900);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_FAVORITES + "/#", 902);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        helper = new Helper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase database = helper.getWritableDatabase();
        if (matcher.match(uri) == 900) {
            database.beginTransaction();
            int rows = 0;
            try {
                for (ContentValues value : values)
                    if (database.insert(Contract.Entry.TABLE_NAME, null, value) != -1) rows++;
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
            return rows;
        }

        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        int match = matcher.match(uri);
        if (match == 900)
            cursor = helper.getReadableDatabase().query(Contract.Entry.TABLE_NAME, strings, s, strings1, null, null, s1);
        else if (match == 902) {
            String[] arguments = new String[]{uri.getLastPathSegment()};
            cursor = helper.getReadableDatabase().query(Contract.Entry.TABLE_NAME, strings, Contract.Entry._ID + " = ? ", arguments, null, null, s1);
        } else throw new IllegalStateException("Unexpected value: " + matcher.match(uri));
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
        int match = matcher.match(uri);
        Uri URI;
        final SQLiteDatabase database = helper.getWritableDatabase();
        if (match == 900) {
            long id = database.insert(Contract.Entry.TABLE_NAME, null, contentValues);
            if (id > 0) {
                ContentValues values = new ContentValues();
                values.put(Contract.Entry.COLUMN_PRIORITY, id);
                String string = Contract.Entry._ID + " =?";
                String[] strings = new String[]{Long.toString(id)};
                database.update(Contract.Entry.TABLE_NAME, values, string, strings);
                URI = ContentUris.withAppendedId(Contract.Entry.CONTENT_URI, id);
            } else throw new SQLException("Failed to insert row into " + uri);
        } else throw new IllegalStateException("Unexpected value: " + match);
        getContext().getContentResolver().notifyChange(uri, null);
        return URI;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        if (s == null) s = "1";
        int rows;
        int match = matcher.match(uri);
        if (match == 900)
            rows = helper.getWritableDatabase().delete(Contract.Entry.TABLE_NAME, s, strings);
        else if (match == 902) {
            String newstring = Contract.Entry._ID + " =?";
            String[] newstrings = {uri.getLastPathSegment()};
            rows = helper.getWritableDatabase().delete(Contract.Entry.TABLE_NAME, newstring, newstrings);
        } else throw new IllegalStateException("Unexpected value: " + matcher.match(uri));
        if (rows != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase database = helper.getWritableDatabase();
        int count;
        if (matcher.match(uri) == 900) count = database.update(Contract.Entry.TABLE_NAME, contentValues, s, strings);
        else throw new UnsupportedOperationException("Unknown uri: " + uri);
        if (count != 0) getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}