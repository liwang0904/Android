package com.example.popularmovies2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 8;

    public static final String DATABASE_NAME = "movieDetails";

    public static final String TABLE_MOVIE = "movies";
    public static final String TABLE_REVIEW = "review";

    public static final String KEY_ID = "id";
    public static final String KEY_POSTER_PATH = "poster";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_RATING = "rating";
    public static final String KEY_RELEASE_DATE = "release";

    public static final String R_KEY_AUTHOR = "author";
    public static final String R_KEY_CONTENT = "content";
    public static final String R_KEY_MOVIE_ID = "movie_id";

    protected DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_MOVIE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT," + KEY_RATING + " TEXT,"
                + KEY_RELEASE_DATE + " TEXT," + KEY_POSTER_PATH + " TEXT" + ")";
        db.execSQL(CREATE_MOVIES_TABLE);

        String CREATE_TABLE_REVIEWS = "CREATE TABLE " + TABLE_REVIEW + "("
                + R_KEY_AUTHOR + " TEXT," + R_KEY_CONTENT + " TEXT," + R_KEY_MOVIE_ID + " TEXT" + ")";
        db.execSQL(CREATE_TABLE_REVIEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
        onCreate(db);
    }

    Movie getMovie(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MOVIE,
                new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_RATING, KEY_RELEASE_DATE, KEY_POSTER_PATH},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Movie movie = new Movie(cursor.getString(5),
                cursor.getString(1), cursor.getString(0), cursor.getString(4),
                cursor.getString(3), cursor.getString(2));
        db.close();
        cursor.close();
        return movie;
    }

    ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> moviesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MOVIE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(
                        cursor.getString(5),
                        cursor.getString(1),
                        cursor.getString(0),
                        cursor.getString(4),
                        cursor.getString(3),
                        cursor.getString(2)
                );
                moviesList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return moviesList;
    }

    void deleteMovie(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIE, KEY_ID + " = ?",
                new String[]{id});
        db.close();
    }

    void deleteReview(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REVIEW, R_KEY_MOVIE_ID + " = ?", new String[]{id});
        db.close();
    }

    void addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, movie.getId());
        values.put(KEY_TITLE, movie.getTitle()); // Movie title
        values.put(KEY_DESCRIPTION, movie.getDescription()); // Movie description
        values.put(KEY_RATING, movie.getUsers_rating()); // Movie rating
        values.put(KEY_RELEASE_DATE, movie.getRelease_date()); // Movie release date
        values.put(KEY_POSTER_PATH, movie.getPoster_path()); // Movie poster path

        db.insert(TABLE_MOVIE, null, values);
        db.close(); // closing database connection
    }

    void addReview(Review review) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(R_KEY_AUTHOR, review.getAuthor());
        values.put(R_KEY_CONTENT, review.getContent());
        values.put(R_KEY_MOVIE_ID, review.getId());

        db.insert(TABLE_REVIEW, null, values);
        db.close();
    }

    ArrayList<Review> getReviews(int theMovieId) {
        ArrayList<Review> reviewList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REVIEW + " WHERE " + R_KEY_MOVIE_ID + " = " + theMovieId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Review review = new Review(
                        "1",
                        cursor.getString(0),
                        cursor.getString(1),
                        "url"
                );
                reviewList.add(review);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reviewList;
    }
}
