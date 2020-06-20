package com.example.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

class Movie implements Parcelable {

    private String poster_path = null;
    private String title;
    private String id;
    private String release_date;
    private String users_rating;
    private String description;

    Movie(String poster_path, String title, String id, String release_date, String users_rating, String description) {
        this.poster_path = poster_path;
        this.title = title;
        this.id = id;
        this.release_date = release_date;
        this.users_rating = users_rating;
        this.description = description;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getUsers_rating() {
        return users_rating;
    }

    public void setUsers_rating(String users_rating) {
        this.users_rating = users_rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected Movie(Parcel in) {
        poster_path = in.readString();
        title = in.readString();
        id = in.readString();
        release_date = in.readString();
        users_rating = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(title);
        dest.writeString(id);
        dest.writeString(release_date);
        dest.writeString(users_rating);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}