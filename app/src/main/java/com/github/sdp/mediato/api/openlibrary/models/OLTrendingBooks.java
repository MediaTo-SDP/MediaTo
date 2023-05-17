package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLTrendingBooks {

    @SerializedName("query")
    private String query;
    @SerializedName("works")
    private List<OLBook> works;
    @SerializedName("days")
    private int days;

    @SerializedName("hours")
    private  int hours;

    public List<OLBook> getWorks() {
        return works;
    }
}

