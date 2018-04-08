package com.example.pratik.moviedb2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;

import com.example.pratik.moviedb2.adapters.MoviesAdapter;
import com.example.pratik.moviedb2.data.Movie;
import com.example.pratik.moviedb2.data.contentprovider.MovieContract;
import com.example.pratik.moviedb2.Interfaces.OnItemClickListener;
import com.example.pratik.moviedb2.utilities.GlobalUtils;
import com.example.pratik.moviedb2.utilities.LayoutUtils;
import com.example.pratik.moviedb2.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        OnItemClickListener {

    private RecyclerView mMainMoviesGridRecyclerView;
    private MoviesAdapter mMoviesGridSupportAdapter;

    private static final String MOVIES_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String MOVIES_IMAGE_SIZE = "w500/";
    private static final String MOVIES_POPULAR = "/movie/popular";
    private static final String MOVIES_TOP_RATED = "/movie/top_rated";
    private static final String MOVIES_FAVORITES = "movie_favorites";
    private String MOVIES_CURRENT_FILTER;

    private TextView errorInternetConnectionTextView, noFavoriteMoviesTextView;
    private ProgressBar loadingProgressBar;

    private ArrayList<Movie> mMainMoviesList = new ArrayList<>();

    private static final String QUERY_MOVIE_FILTER = "movie_filter";
    private int MOVIE_GET_LOADER = 22;
    private int MOVIE_CURSOR_GET_LOADER = 41;
    private int MOVIE_FAVORITES_GET_LOADER = 77;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isAllPermissionsGranted();
        mMainMoviesGridRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMainMoviesGridRecyclerView.setHasFixedSize(true);
        errorInternetConnectionTextView = (TextView) findViewById(R.id.tv_error_no_internet_connection);
        noFavoriteMoviesTextView = (TextView) findViewById(R.id.tv_no_favorite_movies);
        loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        GridLayoutManager layoutManager = new GridLayoutManager(this, LayoutUtils.calculateNoOfColumns(this));
        mMainMoviesGridRecyclerView.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MOVIES_CURRENT_FILTER = sharedPreferences.getString(getString(R.string.pref_current_movies_filter), MOVIES_TOP_RATED);

        if (NetworkUtils.checkInternetConnection(this)) {

            if (MOVIES_CURRENT_FILTER.equals(MOVIES_FAVORITES)) {
                loadMoviesCursorLocalData();
            } else {
                getMovieMainInfo(MOVIES_CURRENT_FILTER);
            }

        } else {

            if (MOVIES_CURRENT_FILTER.equals(MOVIES_FAVORITES)) {
                loadMoviesCursorLocalData();
            } else {
                showNoFavoriteMoviesMessage();
            }

        }
    }
    private String TAG = "PERMISSIONS TAG";

    public boolean isAllPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.
                    permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED ) {
                Log.v(TAG, "Permission is granted > 23");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            Log.v(TAG, "Permission is granted < 23");
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

        } else {
            isAllPermissionsGranted();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedItemId = item.getItemId();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String preferencesKey;

        switch (selectedItemId) {
            case R.id.action_show_popular_movies:
                Toast.makeText(getApplicationContext(), getString(R.string.showing_popular_movies), Toast.LENGTH_SHORT).show();
                preferencesKey = getString(R.string.pref_current_movies_filter);

                if (NetworkUtils.checkInternetConnection(this)) {
                    mMainMoviesGridRecyclerView.setAdapter(null);
                    getMovieMainInfo(MOVIES_POPULAR);
                    GlobalUtils.setCurrentFilterSetting(sharedPreferences, preferencesKey, MOVIES_POPULAR);
                } else
                    showErrorMessage();
                break;

            case R.id.action_show_top_rated_movies:
                Toast.makeText(getApplicationContext(), getString(R.string.showing_top_rated_movies), Toast.LENGTH_SHORT).show();
                preferencesKey = getString(R.string.pref_current_movies_filter);
                if (NetworkUtils.checkInternetConnection(this)) {
                    mMainMoviesGridRecyclerView.setAdapter(null);
                    getMovieMainInfo(MOVIES_TOP_RATED);
                    GlobalUtils.setCurrentFilterSetting(sharedPreferences, preferencesKey, MOVIES_TOP_RATED);
                } else
                    showErrorMessage();
                break;

            case R.id.action_show_favorite_movies:
                Toast.makeText(getApplicationContext(), getString(R.string.showing_favorite_movies), Toast.LENGTH_LONG).show();
                //  if (NetworkUtils.checkInternetConnection(this)) {
                mMainMoviesGridRecyclerView.setAdapter(null);
                preferencesKey = getString(R.string.pref_current_movies_filter);
                loadMoviesCursorLocalData();
                GlobalUtils.setCurrentFilterSetting(sharedPreferences, preferencesKey, MOVIES_FAVORITES);
                //  } else
                // showNoFavoriteMoviesMessage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadMoviesCursorLocalData() {

        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        android.support.v4.content.Loader<Cursor> getCusorLoader = loaderManager.getLoader(MOVIE_CURSOR_GET_LOADER);

        if (getCusorLoader == null) {
            loaderManager.initLoader(MOVIE_CURSOR_GET_LOADER, null, new CallbackLocalQuery());
        } else
            loaderManager.restartLoader(MOVIE_CURSOR_GET_LOADER, null, new CallbackLocalQuery());

    }

    public void showErrorMessage() {

        mMainMoviesGridRecyclerView.setVisibility(View.INVISIBLE);
        errorInternetConnectionTextView.setVisibility(View.VISIBLE);
        if (noFavoriteMoviesTextView.getVisibility() == View.VISIBLE) {
            noFavoriteMoviesTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void showMovieDataView() {

        mMainMoviesGridRecyclerView.setVisibility(View.VISIBLE);
        errorInternetConnectionTextView.setVisibility(View.INVISIBLE);
    }

    public void showNoFavoriteMoviesMessage() {

        mMainMoviesGridRecyclerView.setVisibility(View.INVISIBLE);
        noFavoriteMoviesTextView.setVisibility(View.VISIBLE);
        if (errorInternetConnectionTextView.getVisibility() == View.VISIBLE) {
            errorInternetConnectionTextView.setVisibility(View.INVISIBLE);
        }
    }


    private void getMovieMainInfo(String movieFilter) {

        Bundle argsBundle = new Bundle();
        argsBundle.putString(QUERY_MOVIE_FILTER, movieFilter);

        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<Movie>> getMoviesLoader = loaderManager.getLoader(MOVIE_GET_LOADER);

        if (getMoviesLoader == null) {
            loaderManager.initLoader(MOVIE_GET_LOADER, argsBundle, this);
        } else
            loaderManager.restartLoader(MOVIE_GET_LOADER, argsBundle, this);

    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            ArrayList<Movie> movies = new ArrayList<>();

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                loadingProgressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                String movieFilter = bundle.getString(QUERY_MOVIE_FILTER);

                final String apiKey = BuildConfig.API_KEY;
                URL moviesRequestUrl = NetworkUtils.buildUrl(apiKey, movieFilter);

                String responseJSONString = NetworkUtils.getResponseFromHttp(moviesRequestUrl);

                try {
                    JSONObject parentJSONObject = new JSONObject(responseJSONString);
                    JSONArray moviesArray = parentJSONObject.getJSONArray("results");

                    for (int i = 0; i < moviesArray.length(); i++) {

                        JSONObject childMovieObject = moviesArray.getJSONObject(i);

                        String movieId = childMovieObject.getString("id");
                        String originalTitle = childMovieObject.getString("original_title");
                        String moviePosterUrl = MOVIES_IMAGE_BASE_URL + MOVIES_IMAGE_SIZE + childMovieObject.getString("poster_path");
                        String plotSynopsis = childMovieObject.getString("overview");
                        double userRating = childMovieObject.getDouble("vote_average");
                        String releaseDate = childMovieObject.getString("release_date");

                        movies.add(new Movie(movieId, originalTitle, moviePosterUrl, plotSynopsis, userRating, releaseDate));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return movies;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        if (!data.isEmpty()) {
            showMovieDataView();
            mMoviesGridSupportAdapter = new MoviesAdapter(getApplicationContext(), data, this);
            mMainMoviesGridRecyclerView.setAdapter(mMoviesGridSupportAdapter);

            if (!mMainMoviesList.isEmpty()) mMainMoviesList.clear();
            mMainMoviesList = data;
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onClick(int position) {

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        String movieId = mMainMoviesList.get(position).getId();
        String originalTitle = mMainMoviesList.get(position).getOriginalTitle();
        String moviePosterUrl = mMainMoviesList.get(position).getPosterUrl();
        String plotSynopsis = mMainMoviesList.get(position).getPlotSynopsis();
        double userRating = mMainMoviesList.get(position).getUserRating();
        String releaseDate = mMainMoviesList.get(position).getReleaseDate();

        intent.putExtra(getString(R.string.movie_key), new Movie(movieId, originalTitle, moviePosterUrl, plotSynopsis, userRating, releaseDate));
        startActivity(intent);
    }


    //CURSOR CALLBACK LOADER
    private Cursor moviesCursorData = null;

    private class CallbackLocalQuery implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                Cursor mMoviesData = null;

                @Override
                protected void onStartLoading() {
                    if (mMoviesData != null) {
                        deliverResult(mMoviesData);
                    } else forceLoad();
                }

                @Override
                public Cursor loadInBackground() {

                    try {
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor data) {
                    mMoviesData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            moviesCursorData = data;
            getFavoriteMovies();

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }


    public void getFavoriteMovies() {
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        android.support.v4.content.Loader<ArrayList<Movie>> getFavoriteMoviesLoader = loaderManager.getLoader(MOVIE_FAVORITES_GET_LOADER);

        if (getFavoriteMoviesLoader == null) {
            loaderManager.initLoader(MOVIE_FAVORITES_GET_LOADER, null, new CallbackFavoriteMovies());
        } else
            loaderManager.restartLoader(MOVIE_FAVORITES_GET_LOADER, null, new CallbackFavoriteMovies());
    }


    private class CallbackFavoriteMovies implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
            OnItemClickListener {

        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<ArrayList<Movie>>(getApplicationContext()) {

                ArrayList<Movie> movieArrayList = new ArrayList<>();

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }


                @Override
                public ArrayList<Movie> loadInBackground() {

                    while (moviesCursorData.moveToNext()) {
                        String movieCursorId = moviesCursorData.getString(moviesCursorData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                        String movieOriginalTitle = moviesCursorData.getString(moviesCursorData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
                        String movieUri = moviesCursorData.getString(moviesCursorData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URI));
                        String plotSynopsis = moviesCursorData.getString(moviesCursorData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));
                        double userRating = moviesCursorData.getDouble(moviesCursorData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE));
                        String releaseDate = moviesCursorData.getString(moviesCursorData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
                        movieArrayList.add(new Movie(movieCursorId, movieOriginalTitle, movieUri, plotSynopsis, userRating, releaseDate));
                    }
                    return movieArrayList;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            if (!data.isEmpty()) {
                showMovieDataView();
                mMoviesGridSupportAdapter = new MoviesAdapter(getApplicationContext(), data, this);
                mMainMoviesGridRecyclerView.setAdapter(mMoviesGridSupportAdapter);

                if (!mMainMoviesList.isEmpty()) mMainMoviesList.clear();
                mMainMoviesList = data;
            } else {
                showNoFavoriteMoviesMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

        }

        @Override
        public void onClick(int position) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);

            String movieId = mMainMoviesList.get(position).getId();
            String originalTitle = mMainMoviesList.get(position).getOriginalTitle();
            String moviePosterUrl = mMainMoviesList.get(position).getPosterUrl();
            String plotSynopsis = mMainMoviesList.get(position).getPlotSynopsis();
            double userRating = mMainMoviesList.get(position).getUserRating();
            String releaseDate = mMainMoviesList.get(position).getReleaseDate();

            intent.putExtra(getString(R.string.movie_key), new Movie(movieId, originalTitle, moviePosterUrl, plotSynopsis, userRating, releaseDate));
            startActivity(intent);
        }
    }

}
