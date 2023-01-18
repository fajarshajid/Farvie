package com.fajar.movie.Model;


public class MovieReviewListModel {
    private String avatar_path;
    private String name;
    private String created_at;
    private String content;

    public MovieReviewListModel() {
    }

    public MovieReviewListModel(String avatar_path,
                                String name,
                                String created_at,
                                String content) {
        this.avatar_path = avatar_path;
        this.name = name;
        this.created_at = created_at;
        this.content = content;
    }

    public String getAvatar_path() {
        return avatar_path;
    }
    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }


}
