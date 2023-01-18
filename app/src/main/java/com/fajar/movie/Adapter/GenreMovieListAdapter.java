package com.fajar.movie.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fajar.movie.Model.GenreListModel;
import com.fajar.movie.R;

import java.util.List;


public class GenreMovieListAdapter extends RecyclerView.Adapter<GenreMovieListAdapter.Holder> {

    private List<GenreListModel> mListData;
    private Context mContext;

    public GenreMovieListAdapter(List<GenreListModel> mListData, Context context) {
        this.mListData = mListData;
        this.mContext = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.listitem_movies_genre, parent, false);

        return new Holder(view);
    }




    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final GenreListModel model = mListData.get(position);
        try {
            holder.card_genre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.linear_genre.setBackgroundResource(R.drawable.border_selected);
                    holder.txt_genre_name.setTextColor(mContext.getResources().getColor(R.color.putih));
                    Intent intent = new Intent("genre");
                    intent.putExtra("id", model.getId());
                    intent.putExtra("position", String.valueOf(position));
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            });

            holder.txt_genre_name.setText(model.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private CardView card_genre;
        private LinearLayout linear_genre;
        private TextView txt_genre_name;

        public Holder(View itemView) {
            super(itemView);
            card_genre = itemView.findViewById(R.id.card_genre);
            linear_genre = itemView.findViewById(R.id.linear_genre);
            txt_genre_name = itemView.findViewById(R.id.txt_genre_name);
        }
    }
}
