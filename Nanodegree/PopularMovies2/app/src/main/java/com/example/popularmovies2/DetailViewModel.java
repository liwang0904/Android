package com.example.popularmovies2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.popularmovies2.Database.MovieRoomDatabase;

public class DetailViewModel extends ViewModel {
    private LiveData<Movie> movie;

    public DetailViewModel(MovieRoomDatabase database, String movieId) {
        movie = database.movieDao ().isMovieInDb(movieId);
    }

    public LiveData<Movie> checkIfMovieInDb() {
        return movie;
    }
}