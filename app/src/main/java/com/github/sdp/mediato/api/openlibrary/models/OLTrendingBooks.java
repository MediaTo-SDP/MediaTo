package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLTrendingBooks {

    @SerializedName("query")
    private String query;
    @SerializedName("works")
    private List<OLTrendingBook> works;
    @SerializedName("days")
    private int days;

    @SerializedName("hours")
    private  int hours;

    public List<OLTrendingBook> getWorks() {
        return works;
    }
}

