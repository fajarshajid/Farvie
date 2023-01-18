package com.fajar.movie.Activity;


import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.fajar.movie.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;


public class YoutubeActivity extends AppCompatActivity{

    //STRING
    String name, key;
    CardView card_kembali;
    LinearLayout youtube_linear;
    YouTubePlayerFragment youTubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        int dialogWindowHeight = (int) (displayHeight * 0.4f);
        params.width = dialogWindowWidth;
        params.height = dialogWindowHeight;
        this.getWindow().setAttributes(params);

        try {
            //STRING
            name = getIntent().getStringExtra("name");
            key = getIntent().getStringExtra("key");

            //VARIABLE
            card_kembali = findViewById(R.id.card_kembali);
            youtube_linear = findViewById(R.id.youtube_linear);

            card_kembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.content);

            youTubePlayerFragment.initialize(
                    getString(R.string.apikey_youtube),
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer players, boolean wasRestored)
                        {
                            players.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                @Override
                                public void onLoading() {

                                }

                                @Override
                                public void onLoaded(String s) {

                                }

                                @Override
                                public void onAdStarted() {

                                }

                                @Override
                                public void onVideoStarted() {


                                }

                                @Override
                                public void onVideoEnded() {
                                    if (!wasRestored) {
                                        players.cueVideo(key);
                                    }
                                }

                                @Override
                                public void onError(YouTubePlayer.ErrorReason errorReason) {

                                }
                            });

                            players.cueVideo(key);
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult
                                                                    youTubeInitializationResult)
                        {
                            Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}