package com.fajar.movie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fajar.movie.Activity.MovieActivity;
import com.fajar.movie.Model.MovieListModel;
import com.fajar.movie.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
        View view = inflater.inflate(R.layout.listitem_movie, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final MovieListModel model = mListData.get(position);

        //NUMBER FORMAT
        Locale localeID = new Locale("in", "ID");
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(localeID);
        decimalFormat.setMaximumFractionDigits(1);
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        decimalFormat.setDecimalFormatSymbols(symbols);

        //VOTE AVARAGE
        String str_vote_average = decimalFormat.format(Double.parseDouble(model.getVote_average()));
        holder.vote_average.setText(str_vote_average);

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
                Intent in = new Intent(mContext, MovieActivity.class);
                in.putExtra("adult", model.getAdult());
                in.putExtra("backdrop_path", model.getBackdrop_path());
                in.putExtra("id", model.getId());
                in.putExtra("original_language", model.getOriginal_language());
                in.putExtra("original_title", model.getOriginal_title());
                in.putExtra("overview", model.getOverview());
                in.putExtra("poster_path", model.getPoster_path());
                in.putExtra("popularity", model.getPopularity());
                in.putExtra("title", model.getTitle());
                in.putExtra("release_date", model.getRelease_date());
                in.putExtra("video", model.getVideo());
                in.putExtra("vote_average", model.getVote_average());
                in.putExtra("vote_count", model.getVote_count());
                mContext.startActivity(in);
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
        private TextView vote_average;

        public Holder(View itemView) {
            super(itemView);
            btn_movies = itemView.findViewById(R.id.btn_movies);
            poster_path = itemView.findViewById(R.id.poster_path);
            vote_average = itemView.findViewById(R.id.vote_average);
        }
    }


}
