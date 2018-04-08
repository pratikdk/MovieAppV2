package com.example.pratik.moviedb2.utilities;

import android.content.SharedPreferences;

/**
 * Created by Pratik
 */

public class GlobalUtils {

    public static void setCurrentFilterSetting(SharedPreferences preferences, String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();

    }
}