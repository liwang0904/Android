package com.example.teleprompter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "documents.db";

    public Helper(Context context) {
        super(context, DATABASE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + Contract.Entry.TABLE_NAME + " (" +
                Contract.Entry._ID + " INTEGER PRIMARY KEY, " +
                Contract.Entry.COLUMN_CLOUD_ID + " TEXT, " +
                Contract.Entry.COLUMN_TITLE + " TEXT, " +
                Contract.Entry.COLUMN_TEXT + " TEXT, " +
                Contract.Entry.COLUMN_PRIORITY + " INTEGER, " +
                Contract.Entry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                Contract.Entry.COLUMN_IS_TUTORIAL + " INTEGER" + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.Entry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}