package com.fajar.movie.TrendingList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fajar.movie.MovieList.MovieListModel;
import com.fajar.movie.R;

import java.util.List;

public class TrendingListAdapter extends RecyclerView.Adapter<TrendingListAdapter.Holder> {

    private List<TrendingListModel> mListData;
    private ViewPager2 viewPager;
    private LayoutInflater mInflater;
    private Context mContext;


    public TrendingListAdapter(List<TrendingListModel> mListData, Context mContext, ViewPager2 viewPager) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mListData = mListData;
        this.mContext = mContext;
        this.viewPager = viewPager;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.listitem_trending, parent, false);

        CardView cardView = view.findViewById(R.id.card_view);
        cardView.setElevation(0);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final TrendingListModel model = mListData.get(position);

        //backdrop_path
        Glide
                .with(mContext)
                .load(mContext.getString(R.string.url_image_original) + model.getBackdrop_path())
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.backdrop_path);

        holder.original_title.setText(model.getOriginal_title());
        holder.overview.setText(model.getOverview());

        //poster_path
        Glide
                .with(mContext)
                .load(mContext.getString(R.string.url_image_original) + model.getPoster_path())
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.poster_path);

        holder.backdrop_path.setOnClickListener(new View.OnClickListener() {
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

        private ImageView backdrop_path;
        private TextView original_title;
        private TextView overview;
        private ImageView poster_path;

        public Holder(View itemView) {
            super(itemView);
            backdrop_path = itemView.findViewById(R.id.backdrop_path);
            original_title = itemView.findViewById(R.id.original_title);
            overview = itemView.findViewById(R.id.overview);
            poster_path = itemView.findViewById(R.id.poster_path);

        }
    }


}
