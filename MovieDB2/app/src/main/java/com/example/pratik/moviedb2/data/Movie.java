package com.example.pratik.moviedb2.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Pratik
 */

public class Movie implements Parcelable {

    private String mMovieID;
    private String mMovieTitle;
    private String mMoviePosterUrl;
    private String mMovieSynopsis;
    private double mMovieAvgRating;
    private String mMovieReleaseDate;
    private ArrayList<Review> mMovieReviews;

    protected Movie(Parcel in) {

        mMovieID = in.readString();
        mMovieTitle = in.readString();
        mMoviePosterUrl = in.readString();
        mMovieSynopsis = in.readString();
        mMovieAvgRating = in.readDouble();
        mMovieReleaseDate = in.readString();
        mMovieReviews = in.readArrayList(ClassLoader.getSystemClassLoader());
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

    public String getOriginalTitle() {
        return mMovieTitle;
    }

    public String getPlotSynopsis() {
        return mMovieSynopsis;
    }

    public double getUserRating() {
        return mMovieAvgRating;
    }

    public String getReleaseDate() {
        return mMovieReleaseDate;
    }

    public String getPosterUrl() {
        return mMoviePosterUrl;
    }

    public String getId() {
        return mMovieID;
    }

    public ArrayList<Review> getReviews() {
        return mMovieReviews;
    }

    public Movie(String id, String title, String posterUrl, String synopsis, double avgRating, String releaseDate) {
        this.mMovieID = id;
        this.mMovieTitle = title;
        this.mMoviePosterUrl = posterUrl;
        this.mMovieSynopsis = synopsis;
        this.mMovieAvgRating = avgRating;
        this.mMovieReleaseDate = releaseDate;
        this.mMovieReviews = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMovieID);
        parcel.writeString(mMovieTitle);
        parcel.writeString(mMoviePosterUrl);
        parcel.writeString(mMovieSynopsis);
        parcel.writeDouble(mMovieAvgRating);
        parcel.writeString(mMovieReleaseDate);
        parcel.writeList(mMovieReviews);
    }
}
