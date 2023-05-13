package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLTrendingBook {
    @SerializedName("key")
    private String key;
    @SerializedName("title")
    private String title;
    @SerializedName("first_publish_year")
    private int firstPublishYear;
    @SerializedName("cover_i")
    private int coverI;


    public String getKey() {
        return key.replaceAll("/works/","");
    }

    public int getFirstPublishYear() {
        return firstPublishYear;
    }

    public String getTitle() {
        return title;
    }

    public int getCoverI() {
        return coverI;
    }
}
