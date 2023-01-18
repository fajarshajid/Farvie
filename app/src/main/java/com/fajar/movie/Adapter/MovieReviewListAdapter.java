package com.fajar.movie.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fajar.movie.Model.MovieReviewListModel;
import com.fajar.movie.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class MovieReviewListAdapter extends RecyclerView.Adapter<MovieReviewListAdapter.Holder> {

    private List<MovieReviewListModel> mListData;
    private Context mContext;

    public MovieReviewListAdapter(List<MovieReviewListModel> mListData, Context context) {
        this.mListData = mListData;
        this.mContext = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.listitem_movie_review, parent, false);

        return new Holder(view);
    }




    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final MovieReviewListModel model = mListData.get(position);
        try {
            holder.name.setText(model.getName());

            //CREATED AT
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            Date date = inputFormat.parse(model.getCreated_at().replace("Z","+0000"));
            String str_release_date = outputFormat.format(date);
            holder.created_at.setText(str_release_date);

            holder.content.setText(model.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private CircleImageView avatar_path;
        private TextView name;
        private TextView created_at;
        private TextView content;

        public Holder(View itemView) {
            super(itemView);
            avatar_path = itemView.findViewById(R.id.avatar_path);
            name = itemView.findViewById(R.id.name);
            created_at = itemView.findViewById(R.id.created_at);
            content = itemView.findViewById(R.id.content);
        }
    }
}
