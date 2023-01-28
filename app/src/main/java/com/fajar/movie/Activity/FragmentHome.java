package com.fajar.movie.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fajar.movie.BuildConfig;
import com.fajar.movie.CacheRequest;
import com.fajar.movie.EndlessRecyclerViewScrollListener;
import com.fajar.movie.Adapter.GenreHomeListAdapter;
import com.fajar.movie.Model.GenreListModel;
import com.fajar.movie.Adapter.MovieListAdapter;
import com.fajar.movie.Model.MovieListModel;
import com.fajar.movie.R;
import com.fajar.movie.Server;
import com.fajar.movie.Adapter.TrendingListAdapter;
import com.fajar.movie.Model.TrendingListModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {
    //REQUEST
    private RequestQueue requestQueue;

    CardView btn_cari;

    //REFRESH
    SwipeRefreshLayout swipe;

    //MOVIES TRENDING
    private String url_movies_trending = Server.url_movies_trending + "?api_key=" + Server.api_key;
    ViewPager2 trending_viewPager;
    TextView trending_txt_viewpager;
    ShimmerFrameLayout trending_shimmer;
    private List<TrendingListModel> trendingListModels;
    private TrendingListAdapter trendingListAdapter;
    private int trending_page = 1;

    //GENRE
    private static String url_genre = Server.url_genre + "?api_key=" + Server.api_key;
    private List<GenreListModel> genreListModels;
    private GenreHomeListAdapter genreHomeListAdapter;
    private RecyclerView genre_recyclerview;
    private ShimmerFrameLayout genre_shimmer;
    private LinearLayoutManager linearLayoutManagerGenre;
    private int genre_selected = 0;

    //DISCOVER MOVIES BY GENRE
    private String url_movie = Server.url_movie + "?api_key=" + Server.api_key;
    private List<MovieListModel> movieListModels;
    private MovieListAdapter movieListAdapter;
    private LinearLayout movies_linear;
    private RecyclerView movie_recyclerview;
    private ProgressBar movies_loading;
    private ShimmerFrameLayout movie_shimmer;
    private LinearLayoutManager linearLayoutManagerMovie;
    private int genreid;
    private int movie_page = 1;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            btn_cari = view.findViewById(R.id.btn_cari);
            btn_cari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getActivity(), MovieSearchActivity.class);
                    startActivity(in);
                }
            });

            //REFRESH
            swipe = view.findViewById(R.id.swipe);

            //TRENDING
            trending_viewPager = view.findViewById(R.id.trending_viewPager);
            trending_txt_viewpager = view.findViewById(R.id.trending_txt_viewpager);
            trending_shimmer = view.findViewById(R.id.trending_shimmer);
            trendingListModels = new ArrayList<>();

            //GENRE
            genre_recyclerview = view.findViewById(R.id.genre_recyclerview);
            genre_shimmer = view.findViewById(R.id.genre_shimmer);
            genreListModels = new ArrayList<>();

            //MOVIES
            movies_linear = view.findViewById(R.id.movies_linear);
            movie_recyclerview = view.findViewById(R.id.movie_recyclerview);
            movie_shimmer = view.findViewById(R.id.movie_shimmer);
            movies_loading = view.findViewById(R.id.movies_loading);
            movieListModels = new ArrayList<>();

            //ENDLESS RECYCLERVIEW MOVIE
            try {
                movieListAdapter = new MovieListAdapter(movieListModels, getActivity());
                linearLayoutManagerMovie = new GridLayoutManager(
                        getActivity(), 3,
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
                        movie("popularity.desc", true, true, movie_page, "flatrate", genreid);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            //LOAD TRENDING MOVIES
            trending_movies(trending_page);

            //LOAD GENRE
            genre();

            //swipe
            try {
                swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        btn_cari.requestFocus();

                        //TRENDING
                        trending_page = trending_page + 1;
                        trending_movies(trending_page);

                        //GENRE
                        genre();

                        //MOVIES
                        movieListModels.clear();
                        movie("popularity.desc", true, true, movie_page, "flatrate", genreid);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            //BROADCAST
            try {
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiverGenre,
                        new IntentFilter("genre"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    //BROADCAST GENRE ID
    public BroadcastReceiver broadcastReceiverGenre = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (genre_selected == 0) {
                    genre_selected = 1;

                    //AMBIL ID GENRE
                    genreid = Integer.parseInt(intent.getStringExtra("id"));
                    String position = intent.getStringExtra("position");

                    //KEMBALIKAN KE POSISI AWAL SCROLL
                    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
                        @Override
                        protected int getHorizontalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };
                    smoothScroller.setTargetPosition(Integer.parseInt(position));
                    linearLayoutManagerGenre.startSmoothScroll(smoothScroller);
                    movie_recyclerview.smoothScrollToPosition(0);

                    //PANGGIL MOVIES BERDASARKAN ID GENRE
                    movieListModels.clear();
                    movieListAdapter.notifyDataSetChanged();
                    movie_recyclerview.setVisibility(View.GONE);
                    movie_page = 1;
                    movie("popularity.desc", true, true, movie_page, "flatrate", genreid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    //TRENDING MOVIES
    private void trending_movies(int trending_page) {

        trending_txt_viewpager.setVisibility(View.GONE);
        trending_viewPager.setVisibility(View.GONE);
        trending_shimmer.setVisibility(View.VISIBLE);
        trending_shimmer.startShimmerAnimation();

        CacheRequest request = new CacheRequest(Request.Method.GET, url_movies_trending + "&page=" + trending_page, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                JSONObject jsonObject = null;
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    jsonObject = new JSONObject(jsonString);

                    try {
                        String str_total_results = jsonObject.getString("total_results");
                        int int_total_results = Integer.parseInt(str_total_results);
                        if (int_total_results > 0) {
                            int sizet = trendingListModels.size();
                            if (sizet > 0) {
                                trendingListModels.clear();
                            }

                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < 5; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String adult = object.getString("adult");
                                String backdrop_path = object.getString("backdrop_path");
                                String id = object.getString("id");
                                String title = object.getString("title");
                                String original_language = object.getString("original_language");
                                String original_title = object.getString("original_title");
                                String overview = object.getString("overview");
                                String poster_path = object.getString("poster_path");
                                String media_type = object.getString("media_type");
                                String popularity = object.getString("popularity");
                                String release_date = object.getString("release_date");
                                String video = object.getString("video");
                                String vote_average = object.getString("vote_average");
                                String vote_count = object.getString("vote_count");

                                TrendingListModel trendingListModel = new TrendingListModel();
                                trendingListModel.setAdult(adult);
                                trendingListModel.setBackdrop_path(backdrop_path);
                                trendingListModel.setId(id);
                                trendingListModel.setTitle(title);
                                trendingListModel.setOriginal_language(original_language);
                                trendingListModel.setOriginal_title(original_title);
                                trendingListModel.setOverview(overview);
                                trendingListModel.setPoster_path(poster_path);
                                trendingListModel.setMedia_type(media_type);
                                trendingListModel.setPopularity(popularity);
                                trendingListModel.setRelease_date(release_date);
                                trendingListModel.setVideo(video);
                                trendingListModel.setVote_average(vote_average);
                                trendingListModel.setVote_count(vote_count);

                                trendingListModels.add(trendingListModel);
                                trendingListAdapter = new TrendingListAdapter(trendingListModels, getActivity(), trending_viewPager);
                                trending_viewPager.setAdapter(trendingListAdapter);
                            }

                            try {
                                trending_viewPager.setClipToPadding(false);
                                trending_viewPager.setClipChildren(false);
                                trending_viewPager.setOffscreenPageLimit(trendingListAdapter.getItemCount());
                                trending_viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                                compositePageTransformer.addTransformer(new MarginPageTransformer(10));
                                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                                    @Override
                                    public void transformPage(@NonNull View page, float position) {
                                        float r = 1 - Math.abs(position);
                                        page.setScaleY(0.85f + r * 0.15f);
                                    }
                                });


                                trending_viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                    @Override
                                    public void onPageSelected(int position) {
                                        super.onPageSelected(position);
                                        if (position == 0) {
                                            trending_viewPager.setPageTransformer(compositePageTransformer);
                                            trending_viewPager.setPadding(20, 0, 20, 0);
                                        } else {
                                            trending_viewPager.setPageTransformer(compositePageTransformer);
                                            trending_viewPager.setPadding(30, 0, 30, 0);
                                        }

                                    }

                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                                        trending_txt_viewpager.setText(String.valueOf(position + 1) + "/" + trendingListModels.size());
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            trending_txt_viewpager.setVisibility(View.VISIBLE);
                            trending_viewPager.setVisibility(View.VISIBLE);
                            trending_shimmer.setVisibility(View.GONE);
                            trending_shimmer.stopShimmerAnimation();
                            swipe.setRefreshing(false);
                        }

                    } catch (JSONException e) {
                        trending_txt_viewpager.setVisibility(View.GONE);
                        trending_viewPager.setVisibility(View.GONE);
                        trending_shimmer.setVisibility(View.VISIBLE);
                        trending_shimmer.stopShimmerAnimation();
                        e.printStackTrace();
                        swipe.setRefreshing(false);
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                trending_txt_viewpager.setVisibility(View.GONE);
                trending_viewPager.setVisibility(View.GONE);
                trending_shimmer.setVisibility(View.VISIBLE);
                trending_shimmer.stopShimmerAnimation();
                swipe.setRefreshing(false);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity());
        }
        requestQueue.add(request);
    }

    //GENRE
    private void genre() {
        genre_recyclerview.setVisibility(View.GONE);
        genre_shimmer.setVisibility(View.VISIBLE);
        genre_shimmer.startShimmerAnimation();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url_genre, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            int sizet = genreListModels.size();
                            if (sizet > 0) {
                                genreListModels.clear();
                            }

                            JSONArray jsonArray = response.getJSONArray("genres");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id = object.getString("id");
                                String name = object.getString("name");

                                GenreListModel genreListModel = new GenreListModel();
                                genreListModel.setId(id);
                                genreListModel.setName(name);

                                genreListModels.add(genreListModel);
                                try {
                                    genreHomeListAdapter = new GenreHomeListAdapter(genreListModels, getActivity());
                                    linearLayoutManagerGenre = new GridLayoutManager(
                                            getActivity(), 1,
                                            LinearLayoutManager.HORIZONTAL,
                                            false);
                                    genre_recyclerview.setLayoutManager(linearLayoutManagerGenre);
                                    genre_recyclerview.setItemAnimator(new DefaultItemAnimator());
                                    genre_recyclerview.setAdapter(genreHomeListAdapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            genre_recyclerview.setVisibility(View.VISIBLE);
                            genre_shimmer.setVisibility(View.GONE);
                            genre_shimmer.stopShimmerAnimation();
                            swipe.setRefreshing(false);
                        } catch (JSONException e) {

                            genre_recyclerview.setVisibility(View.GONE);
                            genre_shimmer.setVisibility(View.VISIBLE);
                            genre_shimmer.stopShimmerAnimation();
                            e.printStackTrace();
                            swipe.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        genre_recyclerview.setVisibility(View.GONE);
                        genre_shimmer.setVisibility(View.VISIBLE);
                        genre_shimmer.stopShimmerAnimation();
                        swipe.setRefreshing(false);
                    }
                }) {
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity());
        }
        requestQueue.add(request);
    }

    //MOVIE
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

                            genre_selected = 0;
                            movies_loading.setVisibility(View.GONE);
                            movie_recyclerview.setVisibility(View.VISIBLE);
                            movie_shimmer.setVisibility(View.GONE);
                            movie_shimmer.stopShimmerAnimation();
                            swipe.setRefreshing(false);

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
                            swipe.setRefreshing(false);
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
                        swipe.setRefreshing(false);
                    }
                }) {
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity());
        }
        requestQueue.add(request);


    }


    //BOTTOMSHEET DIALOG
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheetView;

    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_COLLAPSED:

                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:

                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:

                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            bottomSheetDialog.dismiss();
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:

                            break;
                        default:

                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            };

    public void dialog_koneksiterputus() {
        //
        try {
            bottomSheetView = getLayoutInflater().inflate(R.layout.info_tombol_satu, null);
            bottomSheetDialog = new BottomSheetDialog(getActivity());
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
            bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);

            TextView txt_infojudul = bottomSheetView.findViewById(R.id.txt_infojudul);
            TextView txt_infojuduldetail = bottomSheetView.findViewById(R.id.txt_infojuduldetail);
            CardView btn_infokanan = bottomSheetView.findViewById(R.id.btn_infokanan);
            TextView txt_infokanan = bottomSheetView.findViewById(R.id.txt_infokanan);

            txt_infojudul.setText("Ada gangguan!");
            txt_infojuduldetail.setText("Yah, sedang ada masalah pada jaringan. Coba beberapa saat lagi!");
            txt_infokanan.setText("Tutup");


            btn_infokanan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    getActivity().finish();
                }
            });

            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void dialog_kasihbintang() {
        //
        {
            //
            try {
                bottomSheetView = getLayoutInflater().inflate(R.layout.info_tombol_rating, null);
                bottomSheetDialog = new BottomSheetDialog(getActivity());
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
                bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback);

                TextView txt_infojudul = bottomSheetView.findViewById(R.id.txt_infojudul);
                TextView txt_infojuduldetail = bottomSheetView.findViewById(R.id.txt_infojuduldetail);
                CardView btn_bisnisprodkinfokiri = bottomSheetView.findViewById(R.id.btn_infokiri);
                TextView txt_infokiri = bottomSheetView.findViewById(R.id.txt_infokiri);
                CardView btn_infokanan = bottomSheetView.findViewById(R.id.btn_infokanan);
                TextView txt_infokanan = bottomSheetView.findViewById(R.id.txt_infokanan);
                CardView btn_tutup = bottomSheetView.findViewById(R.id.btn_tutup);

                txt_infojudul.setText("Yuk, kasih bintang 5!");
                txt_infojuduldetail.setText("Bintang \u2605\u2605\u2605\u2605\u2605 dari kamu sangat berharga bagi kami. Dukung dan ikuti perkembangan aplikasi ini kedepannya ya!");

                txt_infokiri.setText("Bagikan");
                txt_infokanan.setText("Kasih Bintang!");

                btn_tutup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                btn_bisnisprodkinfokiri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        try {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT,
                                    "Yuk download movie di google playstore, tempat nonton semua anime series, anime movies, film movie barat terlengkap\n\n" +
                                            "Klik link berikut\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                btn_infokanan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.packagename))));
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.show();
                bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
