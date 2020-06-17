package com.example.popularmovies1;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Movie implements Parcelable {
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    private int id;
    private String title;
    private int popularity;
    private int rating;
    private String posterPath;
    private Date releaseDate;
    private String overview;

    public Movie(int id, String title, int popularity, int rating, String posterPath, Date releaseDate, String overview) {
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.rating = rating;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    public Movie() {}

    public Movie(Parcel parcel) {
        id = parcel.readInt();
        title = parcel.readString();
        popularity = parcel.readInt();
        rating = parcel.readInt();
        posterPath = parcel.readString();
        releaseDate = new Date(parcel.readLong());
        overview = parcel.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeInt(this.popularity);
        parcel.writeInt(this.rating);
        parcel.writeString(this.posterPath);
        parcel.writeLong(this.releaseDate.getTime());
        parcel.writeString(this.overview);
    }

    @NonNull
    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ",popularity='" + popularity + '\'' +
                ",voteAvg='" + rating + '\'' +
                ",posterPath'" + posterPath + '\'' +
                ",releaseDate='" + releaseDate.toString() + '\'' +
                ",overview='" + overview + '\'';
    }
}
