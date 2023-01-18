package com.fajar.movie.Model;


public class PreviewListModel {
    private String name;
    private String key;


    public PreviewListModel() {
    }

    public PreviewListModel(String name,
                            String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
