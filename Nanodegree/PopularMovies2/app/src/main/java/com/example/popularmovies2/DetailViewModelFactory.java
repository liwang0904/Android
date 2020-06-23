package com.example.popularmovies2;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.popularmovies2.Database.MovieRoomDatabase;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRoomDatabase mDb;
    private final String mMovieId;

    public DetailViewModelFactory(MovieRoomDatabase database, String movieId) {
        mDb = database;
        mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new DetailViewModel(mDb, mMovieId);
    }
}