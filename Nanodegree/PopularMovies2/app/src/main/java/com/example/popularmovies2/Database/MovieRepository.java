package com.example.popularmovies2.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.popularmovies2.Movie;

import java.util.List;

public class MovieRepository {

    private MovieDao mMovieDao;
    private LiveData<List<Movie>> mFavoriteMovieList;


    MovieRepository(Application application) {
        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(application);
        mMovieDao = db.movieDao();
        mFavoriteMovieList = mMovieDao.getFavoriteMovies();
    }


    LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovieList;
    }

    void insertMovie(final Movie movie) {
        MovieRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMovieDao.insertMovie(movie);
            }
        });
    }

    void deleteMovie(final Movie movie) {
        MovieRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMovieDao.deleteMovie(movie);
            }
        });
    }
}