package com.fajar.movie.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fajar.movie.Adapter.MovieListAdapter;
import com.fajar.movie.EndlessRecyclerViewScrollListener;
import com.fajar.movie.Model.MovieListModel;
import com.fajar.movie.R;
import com.fajar.movie.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MovieSearchActivity extends AppCompatActivity {

    //REQUEST
    private RequestQueue requestQueue;


    //VARIABLE
    CardView btn_kembali;
    EditText txt_cari;
    TextView preview_text;

    //TIMER
    Timer timer;


    //DISCOVER MOVIES BY GENRE
    private String url_search = Server.url_search + "?api_key=" + Server.api_key;
    private List<MovieListModel> movieListModels;
    private MovieListAdapter movieListAdapter;
    private LinearLayout movies_linear;
    private RecyclerView movie_recyclerview;
    private ProgressBar movies_loading;
    private ShimmerFrameLayout movie_shimmer;
    private LinearLayoutManager linearLayoutManagerMovie;
    private int movie_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        try {
            //VARIABLE
            btn_kembali = findViewById(R.id.btn_kembali);
            txt_cari = findViewById(R.id.txt_cari);
            preview_text = findViewById(R.id.preview_text);

            //MOVIES
            movies_linear = findViewById(R.id.movies_linear);
            movie_recyclerview = findViewById(R.id.movie_recyclerview);
            movie_shimmer = findViewById(R.id.movie_shimmer);
            movies_loading = findViewById(R.id.movies_loading);
            movieListModels = new ArrayList<>();

            btn_kembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            //ENDLESS RECYCLERVIEW MOVIE
            try {
                movieListAdapter = new MovieListAdapter(movieListModels, MovieSearchActivity.this);
                linearLayoutManagerMovie = new GridLayoutManager(
                        MovieSearchActivity.this, 3,
                        LinearLayoutManager.VERTICAL,
                        false);
                movie_recyclerview.setLayoutManager(linearLayoutManagerMovie);
                movie_recyclerview.setItemAnimator(new DefaultItemAnimator());
                movie_recyclerview.setAdapter(movieListAdapter);

                //ENDLESS SCROLLVIEW
                movie_recyclerview.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManagerMovie) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        movies_loading.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, 150);
                        movies_linear.setLayoutParams(params);

                        movie_page = movie_page + 1;
                        movie(txt_cari.getText().toString().trim(), movie_page, true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            txt_cari.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //STOP WHEN TYPES
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }

                        //CLEAR LIST
                        movieListModels.clear();
                        movieListAdapter.notifyDataSetChanged();
                        preview_text.setVisibility(View.GONE);
                        movie_recyclerview.setVisibility(View.GONE);
                        movie_shimmer.setVisibility(View.GONE);
                        movie_shimmer.stopShimmerAnimation();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        //WILL SEARCH AFTER DELAY 0.6 SEC WHEN TEXT MORE THEN 1 CHARACTER
                        if(s.length() > 1){
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                movie_page = 1;
                                                movie(txt_cari.getText().toString().trim(), movie_page, true);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }
                            }, 600);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

    private void movie(String query,
                       int page,
                       boolean include_adult) {

        if (movieListModels.size() > 0) {
            preview_text.setVisibility(View.GONE);
            movie_recyclerview.setVisibility(View.VISIBLE);
            movie_shimmer.setVisibility(View.GONE);
            movie_shimmer.stopShimmerAnimation();
        } else {
            preview_text.setVisibility(View.GONE);
            movie_recyclerview.setVisibility(View.GONE);
            movie_shimmer.setVisibility(View.VISIBLE);
            movie_shimmer.startShimmerAnimation();
        }

        final StringRequest request = new StringRequest(Request.Method.GET, url_search +
                "&query=" + query +
                "&page=" + page +
                "&include_adult=" + include_adult,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String adult = object.getString("adult");
                                String backdrop_path = object.getString("backdrop_path");
                                String id = object.getString("id");
                                String title = object.getString("title");
                                String original_language = object.getString("original_language");
                                String original_title = object.getString("original_title");
                                String overview = object.getString("overview");
                                String poster_path = object.getString("poster_path");
                                String popularity = object.getString("popularity");
                                String release_date = object.getString("release_date");
                                String video = object.getString("video");
                                String vote_average = object.getString("vote_average");
                                String vote_count = object.getString("vote_count");

                                MovieListModel movieListModel = new MovieListModel();
                                movieListModel.setAdult(adult);
                                movieListModel.setBackdrop_path(backdrop_path);
                                movieListModel.setId(id);
                                movieListModel.setTitle(title);
                                movieListModel.setOriginal_language(original_language);
                                movieListModel.setOriginal_title(original_title);
                                movieListModel.setOverview(overview);
                                movieListModel.setPoster_path(poster_path);
                                movieListModel.setPopularity(popularity);
                                movieListModel.setRelease_date(release_date);
                                movieListModel.setVideo(video);
                                movieListModel.setVote_average(vote_average);
                                movieListModel.setVote_count(vote_count);

                                movieListModels.add(movieListModel);
                            }

                            if(jsonArray.length() > 0 || movieListModels.size() > 0){
                                movies_loading.setVisibility(View.GONE);
                                movie_recyclerview.setVisibility(View.VISIBLE);
                                movie_shimmer.setVisibility(View.GONE);
                                movie_shimmer.stopShimmerAnimation();

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(0, 0, 0, 0);
                                movies_linear.setLayoutParams(params);

                                movie_recyclerview.setLayoutManager(linearLayoutManagerMovie);
                                movieListAdapter.notifyDataSetChanged();
                            }else{
                                movies_loading.setVisibility(View.GONE);
                                movie_recyclerview.setVisibility(View.GONE);
                                movie_shimmer.setVisibility(View.GONE);
                                movie_shimmer.stopShimmerAnimation();
                                preview_text.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            movies_loading.setVisibility(View.GONE);
                            if (movieListModels.size() > 0) {
                                movie_recyclerview.setVisibility(View.VISIBLE);
                                movie_shimmer.setVisibility(View.GONE);
                                movie_shimmer.stopShimmerAnimation();
                            } else {
                                movie_recyclerview.setVisibility(View.GONE);
                                movie_shimmer.setVisibility(View.VISIBLE);
                                movie_shimmer.startShimmerAnimation();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        movies_loading.setVisibility(View.GONE);
                        if (movieListModels.size() > 0) {
                            movie_recyclerview.setVisibility(View.VISIBLE);
                            movie_shimmer.setVisibility(View.GONE);
                            movie_shimmer.stopShimmerAnimation();
                        } else {
                            movie_recyclerview.setVisibility(View.GONE);
                            movie_shimmer.setVisibility(View.VISIBLE);
                            movie_shimmer.startShimmerAnimation();
                        }
                    }
                }) {
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(MovieSearchActivity.this);
        }
        requestQueue.add(request);


    }

}