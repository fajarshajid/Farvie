package com.fajar.movie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fajar.movie.Activity.YoutubeActivity;
import com.fajar.movie.Model.PreviewListModel;
import com.fajar.movie.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

public class PreviewListAdapter extends RecyclerView.Adapter<PreviewListAdapter.Holder> {
    private List<PreviewListModel> mListData;
    private Context mContext;;

    public PreviewListAdapter(List<PreviewListModel> mListData, Context mContext) {
        this.mListData = mListData;
        this.mContext = mContext;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.listitem_preview, parent, false);

        return new Holder(view);
    }

    private YouTubePlayer youTubePlayer;

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final PreviewListModel model = mListData.get(position);


        holder.preview_thumbnail.initialize(mContext.getString(R.string.apikey_youtube), new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(model.getKey());
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        Log.e("TAG", "Youtube Thumbnail Error");
                        holder.preview_card.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("TAG", "Youtube Initialization Failure");

            }
        });

        holder.preview_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, YoutubeActivity.class);
                in.putExtra("name", model.getName());
                in.putExtra("key", model.getKey());
                mContext.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private CardView preview_card;
        private YouTubeThumbnailView preview_thumbnail;

        public Holder(View itemView) {
            super(itemView);
            preview_card = itemView.findViewById(R.id.preview_card);
            preview_thumbnail = itemView.findViewById(R.id.preview_thumbnail);
        }

    }


}
