package com.fajar.movie.Model;


public class MovieListModel {
    private String adult;
    private String backdrop_path;
    private String id;
    private String title;
    private String original_language;
    private String original_title;
    private String overview;
    private String poster_path;
    private String popularity;
    private String release_date;
    private String video;
    private String vote_average;
    private String vote_count;


    public MovieListModel() {
    }

    public MovieListModel(String adult,
                          String backdrop_path,
                          String id,
                          String title,
                          String original_language,
                          String original_title,
                          String overview,
                          String poster_path,
                          String popularity,
                          String release_date,
                          String video,
                          String vote_average,
                          String vote_count) {
        this.adult = adult;
        this.backdrop_path = backdrop_path;
        this.id = id;
        this.title = title;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.popularity = popularity;
        this.release_date = release_date;
        this.video = video;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
    }

    public String getAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }


    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

}
