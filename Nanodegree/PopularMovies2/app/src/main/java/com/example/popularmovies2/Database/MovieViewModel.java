package com.example.popularmovies2.Database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies2.Movie;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private MovieRepository movieRepository;

    private LiveData<List<Movie>> favoriteMovieList;

    public MovieViewModel(Application application) {
        super(application);
        movieRepository = new MovieRepository(application);
        favoriteMovieList = movieRepository.getFavoriteMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovieList() {
        return favoriteMovieList;
    }

    public void insert(Movie movie) {
        movieRepository.insertMovie(movie);
    }

    public void delete(Movie movie) {
        movieRepository.deleteMovie(movie);
    }
}