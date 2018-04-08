package com.example.pratik.moviedb2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pratik.moviedb2.Interfaces.OnItemClickListener;
import com.example.pratik.moviedb2.R;
import com.example.pratik.moviedb2.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Pratik
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private ArrayList<Movie> mMovieList;
    private Context mContext;
    private final OnItemClickListener mClickListener;

    public MoviesAdapter(Context context, ArrayList<Movie> moviesList, OnItemClickListener clickListener) {
        this.mContext = context;
        this.mMovieList = moviesList;
        this.mClickListener = clickListener;
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView movieThumbnailImageView;
        private TextView movieTitle;
        private TextView movieDate;

        public MovieHolder(View itemView) {
            super(itemView);

            movieThumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            movieDate = (TextView) itemView.findViewById(R.id.movie_thumb_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mClickListener.onClick(clickedPosition);

        }
    }


    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToRoot = false;

        View movieView = inflater.inflate(R.layout.list_item, parent, shouldAttachToRoot);
        MovieHolder viewHolder = new MovieHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie clickedMovie = mMovieList.get(position);

        String title = clickedMovie.getOriginalTitle();

        String releaseDate = clickedMovie.getReleaseDate();
        String releaseDateArray[] = releaseDate.split("-");
        String releaseYear = releaseDateArray[0];

        holder.movieTitle.setText(title);
        holder.movieDate.setText(releaseYear);

        String movieThumbUrl = clickedMovie.getPosterUrl();
        Picasso.with(mContext)
                .load(movieThumbUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder_error)
                .into(holder.movieThumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }
}
