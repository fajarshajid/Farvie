package com.fajar.movie.Activity;

import android.os.Bundle;
import android.view.View;
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

public class MovieByGenreActivity extends AppCompatActivity {

    //REQUEST
    private RequestQueue requestQueue;

    //STRING GENRE
    int id;
    String name;

    //VARIABLE
    CardView btn_kembali;
    TextView genre_name;
    CardView btn_cari;

    //DISCOVER MOVIES BY GENRE
    private String url_movie = Server.url_movie + "?api_key=" + Server.api_key;
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
        setContentView(R.layout.activity_movie_by_genre);

        try {
            //STRING
            id = Integer.parseInt(getIntent().getStringExtra("id"));
            name = getIntent().getStringExtra("name");

            //VARIABLE
            btn_kembali = findViewById(R.id.btn_kembali);
            genre_name = findViewById(R.id.genre_name);
            btn_cari = findViewById(R.id.btn_cari);


            //MOVIES
            movies_linear = findViewById(R.id.movies_linear);
            movie_recyclerview = findViewById(R.id.movie_recyclerview);
            movie_shimmer = findViewById(R.id.movie_shimmer);
            movies_loading = findViewById(R.id.movies_loading);
            movieListModels = new ArrayList<>();

            //SET VALUE
            genre_name.setText(name);

            btn_kembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            btn_cari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            //ENDLESS RECYCLERVIEW MOVIE
            try {
                movieListAdapter = new MovieListAdapter(movieListModels, MovieByGenreActivity.this);
                linearLayoutManagerMovie = new GridLayoutManager(
                        MovieByGenreActivity.this, 3,
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
                        movie("popularity.desc", true, true, movie_page, "flatrate", id);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            movie_page = 1;
            movie("popularity.desc", true, true, movie_page, "flatrate", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void movie(String sort_by,
                       boolean include_adult,
                       boolean include_video,
                       int movie_page,
                       String with_watch_monetization_types,
                       int with_genres) {

        if (movieListModels.size() > 0) {
            movie_recyclerview.setVisibility(View.VISIBLE);
            movie_shimmer.setVisibility(View.GONE);
            movie_shimmer.stopShimmerAnimation();
        } else {
            movie_recyclerview.setVisibility(View.GONE);
            movie_shimmer.setVisibility(View.VISIBLE);
            movie_shimmer.startShimmerAnimation();
        }

        final StringRequest request = new StringRequest(Request.Method.GET, url_movie +
                "&sort_by=" + sort_by +
                "&include_adult=" + include_adult +
                "&include_video=" + include_video +
                "&page=" + movie_page +
                "&with_watch_monetization_types=" + with_watch_monetization_types +
                "&with_genres=" + with_genres,
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
            requestQueue = Volley.newRequestQueue(MovieByGenreActivity.this);
        }
        requestQueue.add(request);


    }

}