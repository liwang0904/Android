package com.example.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    String title;
    String overview;
    String posterUrl;
    String userRating;
    String releaseDate;
    String id;

    public Movie() {}

    private Movie(Parcel in) {
        title = in.readString();
        overview = in.readString();
        posterUrl = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
        id = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(posterUrl);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
        parcel.writeString(id);
    }
}
