package com.example.pratik.moviedb2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pratik.moviedb2.R;
import com.example.pratik.moviedb2.data.Review;

import java.util.ArrayList;

/**
 * Created by Pratik
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private ArrayList<Review> mReviewsList;
    private Context mContext;

    public ReviewsAdapter(Context context, ArrayList<Review> reviewList) {
        this.mContext = context;
        this.mReviewsList = reviewList;
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthor;
        private TextView reviewContent;
        private View reviewSeperator;

        public ReviewHolder(View itemView) {
            super(itemView);

            reviewAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            reviewContent = (TextView) itemView.findViewById(R.id.tv_review);
            reviewSeperator = itemView.findViewById(R.id.review_divider);
        }
    }


    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToRoot = false;

        View movieView = inflater.inflate(R.layout.list_item_reviews, parent, shouldAttachToRoot);
        ReviewHolder viewHolder = new ReviewHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {

        String reviewAuthor = mReviewsList.get(position).getAuthor();
        holder.reviewAuthor.setText(reviewAuthor);

        String reviewText = mReviewsList.get(position).getContent();
        holder.reviewContent.setText(reviewText);

        if (mReviewsList.size() - 1 == position) {
            holder.reviewSeperator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }
}
