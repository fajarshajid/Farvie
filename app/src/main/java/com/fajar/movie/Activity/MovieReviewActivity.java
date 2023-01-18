package com.fajar.movie.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.fajar.movie.Adapter.MovieReviewListAdapter;
import com.fajar.movie.EndlessRecyclerViewScrollListener;
import com.fajar.movie.Model.MovieListModel;
import com.fajar.movie.Model.MovieReviewListModel;
import com.fajar.movie.R;
import com.fajar.movie.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieReviewActivity extends AppCompatActivity {

    //REQUEST
    private RequestQueue requestQueue;

    //STRING MOVIE
    int id;
    String title;

    //VARIABLE
    CardView btn_kembali;
    TextView txt_title;

    //DISCOVER MOVIES BY GENRE
    private String url_review = Server.url_review;
    private List<MovieReviewListModel> movieReviewListModels;
    private MovieReviewListAdapter movieReviewListAdapter;
    private LinearLayout review_linear;
    private RecyclerView review_recyclerview;
    private ProgressBar review_loading;
    private ShimmerFrameLayout review_shimmer;
    private LinearLayoutManager linearLayoutManagerMovie;
    private int review_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review);

        try {
            //STRING
            id = Integer.parseInt(getIntent().getStringExtra("id"));
            title = getIntent().getStringExtra("title");

            //VARIABLE
            btn_kembali = findViewById(R.id.btn_kembali);
            txt_title = findViewById(R.id.title);


            //MOVIES
            review_linear = findViewById(R.id.review_linear);
            review_recyclerview = findViewById(R.id.review_recyclerview);
            review_shimmer = findViewById(R.id.review_shimmer);
            review_loading = findViewById(R.id.review_loading);
            movieReviewListModels = new ArrayList<>();

            //SET VALUE
            txt_title.setText(title);

            btn_kembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            //ENDLESS RECYCLERVIEW MOVIE
            try {
                movieReviewListAdapter = new MovieReviewListAdapter(movieReviewListModels, MovieReviewActivity.this);
                linearLayoutManagerMovie = new GridLayoutManager(
                        MovieReviewActivity.this, 1,
                        LinearLayoutManager.VERTICAL,
                        false);
                review_recyclerview.setLayoutManager(linearLayoutManagerMovie);
                review_recyclerview.setItemAnimator(new DefaultItemAnimator());
                review_recyclerview.setAdapter(movieReviewListAdapter);

                //ENDLESS SCROLLVIEW
                review_recyclerview.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManagerMovie) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        review_loading.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, 150);
                        review_linear.setLayoutParams(params);

                        review_page = review_page + 1;
                        review(id,  review_page);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            review_page = 1;
            review(id,  review_page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void review(int id,
                       int review_page) {

        if (movieReviewListModels.size() > 0) {
            review_recyclerview.setVisibility(View.VISIBLE);
            review_shimmer.setVisibility(View.GONE);
            review_shimmer.stopShimmerAnimation();
        } else {
            review_recyclerview.setVisibility(View.GONE);
            review_shimmer.setVisibility(View.VISIBLE);
            review_shimmer.startShimmerAnimation();
        }

        final StringRequest request = new StringRequest(Request.Method.GET, url_review + id + "/reviews?api_key=" + Server.api_key + "&page=" + review_page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArrayResult.length(); i++) {
                                JSONObject object = jsonArrayResult.getJSONObject(i);

                                String author = object.getString("author");
                                String author_details = object.getString("author_details");
                                JSONObject obj_author_details = new JSONObject(author_details);
                                String avatar_path = obj_author_details.getString("avatar_path");
                                String created_at = object.getString("created_at");
                                String content = object.getString("content");



                                MovieReviewListModel movieReviewListModel = new MovieReviewListModel();
                                movieReviewListModel.setAvatar_path(avatar_path);
                                movieReviewListModel.setName(author);
                                movieReviewListModel.setCreated_at(created_at);
                                movieReviewListModel.setContent(content);

                                movieReviewListModels.add(movieReviewListModel);
                            }

                            review_loading.setVisibility(View.GONE);
                            review_recyclerview.setVisibility(View.VISIBLE);
                            review_shimmer.setVisibility(View.GONE);
                            review_shimmer.stopShimmerAnimation();

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 0);
                            review_linear.setLayoutParams(params);

                            review_recyclerview.setLayoutManager(linearLayoutManagerMovie);
                            movieReviewListAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            review_loading.setVisibility(View.GONE);
                            if (movieReviewListModels.size() > 0) {
                                review_recyclerview.setVisibility(View.VISIBLE);
                                review_shimmer.setVisibility(View.GONE);
                                review_shimmer.stopShimmerAnimation();
                            } else {
                                review_recyclerview.setVisibility(View.GONE);
                                review_shimmer.setVisibility(View.VISIBLE);
                                review_shimmer.startShimmerAnimation();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        review_loading.setVisibility(View.GONE);
                        if (movieReviewListModels.size() > 0) {
                            review_recyclerview.setVisibility(View.VISIBLE);
                            review_shimmer.setVisibility(View.GONE);
                            review_shimmer.stopShimmerAnimation();
                        } else {
                            review_recyclerview.setVisibility(View.GONE);
                            review_shimmer.setVisibility(View.VISIBLE);
                            review_shimmer.startShimmerAnimation();
                        }
                    }
                }) {
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(MovieReviewActivity.this);
        }
        requestQueue.add(request);


    }

}