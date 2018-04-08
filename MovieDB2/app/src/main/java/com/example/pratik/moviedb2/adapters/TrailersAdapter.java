package com.example.pratik.moviedb2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pratik.moviedb2.Interfaces.OnItemClickListener;
import com.example.pratik.moviedb2.R;
import com.example.pratik.moviedb2.data.Trailer;

import java.util.ArrayList;

/**
 * Created by Pratik
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersHolder> {

    private ArrayList<Trailer> mTrailersList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;

    public TrailersAdapter(Context context, ArrayList<Trailer> trailersList, OnItemClickListener ItemClickListener) {
        this.mContext = context;
        this.mTrailersList = trailersList;
        this.mItemClickListener = ItemClickListener;
    }

    public class TrailersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView trailerTitle;
        private View trailerSeperator;


        public TrailersHolder(View itemView) {
            super(itemView);

            trailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            trailerSeperator = itemView.findViewById(R.id.trailer_divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mItemClickListener.onClick(position);
        }
    }


    @Override
    public TrailersHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToRoot = false;

        View movieView = inflater.inflate(R.layout.list_item_trailers, parent, shouldAttachToRoot);
        TrailersHolder viewHolder = new TrailersHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailersHolder holder, int position) {

        String trailerName = mTrailersList.get(position).getName();
        holder.trailerTitle.setText(trailerName);

        // hide divider when it is the last item.
        if (mTrailersList.size() - 1 == position) {
            holder.trailerSeperator.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mTrailersList.size();
    }
}
