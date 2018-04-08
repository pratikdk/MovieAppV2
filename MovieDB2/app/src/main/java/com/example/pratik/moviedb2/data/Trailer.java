package com.example.pratik.moviedb2.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pratik
 */

public class Trailer implements Parcelable {

    private String mTrailerName;
    private String key;

    public String getName() {
        return mTrailerName;
    }

    public String getKey() {
        return key;
    }


    public Trailer(String name, String key) {
        this.mTrailerName = name;
        this.key = key;
    }

    protected Trailer(Parcel in) {
        mTrailerName = in.readString();
        key = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTrailerName);
        parcel.writeString(key);
    }
}