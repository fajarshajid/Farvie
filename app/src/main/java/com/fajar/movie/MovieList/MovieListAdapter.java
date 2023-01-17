package com.fajar.movie.MovieList;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fajar.movie.R;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.Holder> {

    private List<MovieListModel> mListData;
    private Context mContext;


    public MovieListAdapter(List<MovieListModel> mListData, Context mContext) {
        this.mListData = mListData;
        this.mContext = mContext;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.listitem_movies, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final MovieListModel model = mListData.get(position);

        //poster_path
        Glide
                .with(mContext)
                .load(mContext.getString(R.string.url_image_original) + model.getPoster_path())
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.movies_shimmer)
                .into(holder.poster_path);

        holder.btn_movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private LinearLayout btn_movies;
        private ImageView poster_path;

        public Holder(View itemView) {
            super(itemView);
            btn_movies = itemView.findViewById(R.id.btn_movies);
            poster_path = itemView.findViewById(R.id.poster_path);

        }
    }


}
