package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

public class OLSearchBook {
    @SerializedName("key")
    private String key;
    @SerializedName("title")
    private String title;
    @SerializedName("first_publish_year")
    private Integer firstPublishYear;
    @SerializedName("cover_i")
    private Integer coverId;

    public String getKey() {
        return key.replaceAll("/works/", "");
    }

    public String getTitle() {
        return title;
    }

    public Integer getFirstPublishYear() {
        return firstPublishYear;
    }

    public Integer getCoverId() {
        return coverId;
    }
}
