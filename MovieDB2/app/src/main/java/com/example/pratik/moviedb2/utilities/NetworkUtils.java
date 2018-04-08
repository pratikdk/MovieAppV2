package com.example.pratik.moviedb2.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Pratik
 */

public class NetworkUtils {

    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3";
    private static final String MOVIES_API_KEY = "api_key";

    public static URL buildUrl(String apiKey, String movieType) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + movieType).buildUpon()
                .appendQueryParameter(MOVIES_API_KEY, apiKey).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v("URL", "Built URL: " + url);

        return url;

    }

    public static URL buildReviewsAndVideosUrl(String apiKey, String movie, String movieId, String reviewsOrVideos) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + movie + movieId + reviewsOrVideos)
                .buildUpon().appendQueryParameter(MOVIES_API_KEY, apiKey).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildFavoriteVideosUrl(String apiKey, String movie, String movieId) {

        Uri builtUri = Uri.parse(MOVIES_BASE_URL + movie + movieId).buildUpon()
                .appendQueryParameter(MOVIES_API_KEY, apiKey).build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttp(URL url) {

        HttpURLConnection connection = null;
        String returnedString = "";
        try {
            connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                returnedString = scanner.next();
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return returnedString;

    }

    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

}
