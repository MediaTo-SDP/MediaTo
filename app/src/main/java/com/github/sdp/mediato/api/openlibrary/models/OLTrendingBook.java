package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLTrendingBook {
    @SerializedName("key")
    private String key;

    public String getKey() {
        return key;
    }
}
