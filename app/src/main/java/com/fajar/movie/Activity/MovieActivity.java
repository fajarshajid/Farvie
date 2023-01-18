package com.fajar.movie.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fajar.movie.Adapter.MovieGenreListAdapter;
import com.fajar.movie.Adapter.RelatedListAdapter;
import com.fajar.movie.CacheRequest;
import com.fajar.movie.Model.GenreListModel;
import com.fajar.movie.Adapter.PreviewListAdapter;
import com.fajar.movie.Model.MovieListModel;
import com.fajar.movie.Model.PreviewListModel;
import com.fajar.movie.R;
import com.fajar.movie.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieActivity extends AppCompatActivity {

    //REQUEST
    private RequestQueue requestQueue;

    //STRING
    String adult, backdrop_path, id, original_language, original_title, overview, poster_path, popularity, title, release_date, video, vote_average, vote_count;

    //VARIABLE
    CardView btn_kembali;
    CardView btn_cari;
    ImageView img_backdrop_path;
    ImageView img_poster_path;
    CardView card_review;
    TextView txt_release_date;
    TextView txt_review_name;
    TextView txt_vote_average;
    TextView txt_original_title;
    TextView txt_overview;

    //GENRE
    private static String url_movie_genre = Server.url_movie_genre;
    private List<GenreListModel> genreListModels;
    private MovieGenreListAdapter movieGenreListAdapter;
    private RecyclerView genre_recyclerview;
    private ShimmerFrameLayout genre_shimmer;

    //PREVIEW
    private static String url_preview = Server.url_preview;
    private List<PreviewListModel> previewListModels;
    private PreviewListAdapter previewListAdapter;
    private RecyclerView preview_recyclerview;
    private TextView preview_text;
    private ShimmerFrameLayout preview_shimmer;

    //RELATED
    private static String url_related = Server.url_related;
    private List<MovieListModel> movieListModels;
    private RelatedListAdapter relatedListAdapter;
    private RecyclerView related_recyclerview;
    private ShimmerFrameLayout related_shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        try {
            //STRING
            adult = getIntent().getStringExtra("adult");
            backdrop_path = getIntent().getStringExtra("backdrop_path");
            id = getIntent().getStringExtra("id");
            original_language = getIntent().getStringExtra("original_language");
            original_title = getIntent().getStringExtra("original_title");
            overview = getIntent().getStringExtra("overview");
            poster_path = getIntent().getStringExtra("poster_path");
            popularity = getIntent().getStringExtra("popularity");
            title = getIntent().getStringExtra("title");
            release_date = getIntent().getStringExtra("release_date");
            video = getIntent().getStringExtra("video");
            vote_average = getIntent().getStringExtra("vote_average");
            vote_count = getIntent().getStringExtra("vote_count");

            //VARIABLE
            btn_kembali = findViewById(R.id.btn_kembali);
            btn_cari = findViewById(R.id.btn_cari);
            img_backdrop_path = findViewById(R.id.backdrop_path);
            img_poster_path = findViewById(R.id.poster_path);
            card_review = findViewById(R.id.card_review);
            txt_release_date = findViewById(R.id.release_date);
            txt_review_name = findViewById(R.id.txt_review_name);
            txt_vote_average = findViewById(R.id.vote_average);
            txt_original_title = findViewById(R.id.original_title);
            txt_overview = findViewById(R.id.overview);

            btn_kembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            btn_cari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MovieActivity.this, MovieSearchActivity.class);
                    startActivity(in);
                }
            });

            card_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MovieActivity.this, MovieReviewActivity.class);
                    in.putExtra("id", id);
                    in.putExtra("title", title);
                    startActivity(in);
                }
            });

            //NUMBER FORMAT
            Locale localeID = new Locale("in", "ID");
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(localeID);
            decimalFormat.setMaximumFractionDigits(1);
            DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            symbols.setCurrencySymbol("");
            decimalFormat.setDecimalFormatSymbols(symbols);

            //SET VOTE AVARAGE
            String str_vote_average = decimalFormat.format(Double.parseDouble(vote_average));
            txt_vote_average.setText(str_vote_average);

            //DATE
            if (release_date.length() > 4) {
                txt_release_date.setVisibility(View.VISIBLE);
                DateFormat outputFormat = new SimpleDateFormat("yyyy", Locale.US);
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(release_date);
                String str_release_date = outputFormat.format(date);
                txt_release_date.setText(str_release_date);
            } else {
                txt_release_date.setVisibility(View.GONE);
            }


            //SET TITLE & OVERVIEW
            txt_original_title.setText(title);
            txt_overview.setText(overview);

            //backdrop_path
            Glide
                    .with(this)
                    .load(getString(R.string.url_image_original) + backdrop_path)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(img_backdrop_path);

            //poster_path
            Glide
                    .with(this)
                    .load(getString(R.string.url_image_original) + poster_path)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.movies_shimmer)
                    .into(img_poster_path);

            //PREVIEW
            preview_recyclerview = findViewById(R.id.preview_recyclerview);
            preview_text = findViewById(R.id.preview_text);
            preview_shimmer = findViewById(R.id.preview_shimmer);
            previewListModels = new ArrayList<>();

            //GENRE
            genre_recyclerview = findViewById(R.id.genre_recyclerview);
            genre_shimmer = findViewById(R.id.genre_shimmer);
            genreListModels = new ArrayList<>();

            //RELATED
            related_recyclerview = findViewById(R.id.related_recyclerview);
            related_shimmer = findViewById(R.id.related_shimmer);
            movieListModels = new ArrayList<>();

            genre();
            preview();
            related();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //GENRE
    private void genre() {
        genre_recyclerview.setVisibility(View.GONE);
        genre_shimmer.setVisibility(View.VISIBLE);
        genre_shimmer.startShimmerAnimation();

        CacheRequest request = new CacheRequest(Request.Method.GET, url_movie_genre + id + "?api_key=" + Server.api_key, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                JSONObject jsonObject = null;
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    jsonObject = new JSONObject(jsonString);

                    try {
                        int sizet = genreListModels.size();
                        if (sizet > 0) {
                            genreListModels.clear();
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray("genres");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String id = object.getString("id");
                            String name = object.getString("name");

                            GenreListModel genreListModel = new GenreListModel();
                            genreListModel.setId(id);
                            genreListModel.setName(name);

                            genreListModels.add(genreListModel);
                            movieGenreListAdapter = new MovieGenreListAdapter(genreListModels, MovieActivity.this);
                            LinearLayoutManager linearLayoutManagerGenre = new GridLayoutManager(
                                    MovieActivity.this, 1,
                                    LinearLayoutManager.HORIZONTAL,
                                    false);
                            genre_recyclerview.setLayoutManager(linearLayoutManagerGenre);
                            genre_recyclerview.setItemAnimator(new DefaultItemAnimator());
                            genre_recyclerview.setAdapter(movieGenreListAdapter);
                        }

                        genre_recyclerview.setVisibility(View.VISIBLE);
                        genre_shimmer.setVisibility(View.GONE);
                        genre_shimmer.stopShimmerAnimation();
                    } catch (JSONException e) {
                        genre_recyclerview.setVisibility(View.GONE);
                        genre_shimmer.setVisibility(View.VISIBLE);
                        genre_shimmer.stopShimmerAnimation();
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                genre_recyclerview.setVisibility(View.GONE);
                genre_shimmer.setVisibility(View.VISIBLE);
                genre_shimmer.stopShimmerAnimation();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(MovieActivity.this);
        }
        requestQueue.add(request);
    }

    //PREVIEW
    private void preview() {
        preview_recyclerview.setVisibility(View.GONE);
        preview_text.setVisibility(View.GONE);
        preview_shimmer.setVisibility(View.VISIBLE);
        preview_shimmer.startShimmerAnimation();

        CacheRequest request = new CacheRequest(Request.Method.GET, url_preview + id + "/videos?api_key=" + Server.api_key, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                JSONObject jsonObject = null;
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    jsonObject = new JSONObject(jsonString);

                    try {
                        int sizet = previewListModels.size();
                        if (sizet > 0) {
                            previewListModels.clear();
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name");
                                String key = object.getString("key");

                                PreviewListModel previewListModel = new PreviewListModel();
                                previewListModel.setName(name);
                                previewListModel.setKey(key);

                                previewListModels.add(previewListModel);

                                Log.d("TAG", "" + jsonArray.length());

                                previewListAdapter = new PreviewListAdapter(previewListModels, MovieActivity.this);
                                LinearLayoutManager linearLayoutManager = new GridLayoutManager(
                                        MovieActivity.this, 1,
                                        LinearLayoutManager.HORIZONTAL,
                                        false);
                                preview_recyclerview.setLayoutManager(linearLayoutManager);
                                preview_recyclerview.setItemAnimator(new DefaultItemAnimator());
                                preview_recyclerview.setAdapter(previewListAdapter);
                            }

                            preview_recyclerview.setVisibility(View.VISIBLE);
                            preview_shimmer.setVisibility(View.GONE);
                            preview_text.setVisibility(View.GONE);
                            preview_shimmer.stopShimmerAnimation();
                        } else {
                            preview_recyclerview.setVisibility(View.GONE);
                            preview_text.setVisibility(View.VISIBLE);
                            preview_shimmer.setVisibility(View.GONE);
                            preview_shimmer.stopShimmerAnimation();
                        }
                    } catch (JSONException e) {
                        preview_recyclerview.setVisibility(View.GONE);
                        preview_shimmer.setVisibility(View.GONE);
                        preview_text.setVisibility(View.VISIBLE);
                        preview_shimmer.stopShimmerAnimation();
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    preview_recyclerview.setVisibility(View.GONE);
                    preview_shimmer.setVisibility(View.GONE);
                    preview_text.setVisibility(View.VISIBLE);
                    preview_shimmer.stopShimmerAnimation();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                preview_recyclerview.setVisibility(View.GONE);
                preview_shimmer.setVisibility(View.GONE);
                preview_text.setVisibility(View.VISIBLE);
                preview_shimmer.stopShimmerAnimation();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(request);
    }

    //RELATED
    private void related() {
        related_recyclerview.setVisibility(View.GONE);
        related_shimmer.setVisibility(View.VISIBLE);
        related_shimmer.startShimmerAnimation();

        CacheRequest request = new CacheRequest(Request.Method.GET, url_related + id + "/similar" + "?api_key=" + Server.api_key, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                JSONObject jsonObject = null;
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    jsonObject = new JSONObject(jsonString);

                    try {
                        int sizet = movieListModels.size();
                        if (sizet > 0) {
                            movieListModels.clear();
                        }

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
                            relatedListAdapter = new RelatedListAdapter(movieListModels, MovieActivity.this);
                            LinearLayoutManager linearLayoutManager = new GridLayoutManager(
                                    MovieActivity.this, 1,
                                    LinearLayoutManager.HORIZONTAL,
                                    false);
                            related_recyclerview.setLayoutManager(linearLayoutManager);
                            related_recyclerview.setItemAnimator(new DefaultItemAnimator());
                            related_recyclerview.setAdapter(relatedListAdapter);
                        }

                        related_recyclerview.setVisibility(View.VISIBLE);
                        related_shimmer.setVisibility(View.GONE);
                        related_shimmer.stopShimmerAnimation();
                    } catch (JSONException e) {
                        related_recyclerview.setVisibility(View.GONE);
                        related_shimmer.setVisibility(View.VISIBLE);
                        related_shimmer.stopShimmerAnimation();
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                related_recyclerview.setVisibility(View.GONE);
                related_shimmer.setVisibility(View.VISIBLE);
                related_shimmer.stopShimmerAnimation();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(MovieActivity.this);
        }
        requestQueue.add(request);
    }
}