package com.example.pratik.moviedb2.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pratik
 */

public class Review implements Parcelable {

    private String mReviewAuthor;
    private String mReviewContent;

    public Review(String author, String content) {
        this.mReviewAuthor = author;
        this.mReviewContent = content;
    }

    protected Review(Parcel in) {
        mReviewAuthor = in.readString();
        mReviewContent = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return mReviewAuthor;
    }

    public String getContent() {
        return mReviewContent;
    }

    public void setContent(String content) {
        this.mReviewContent = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mReviewAuthor);
        parcel.writeString(mReviewContent);
    }
}