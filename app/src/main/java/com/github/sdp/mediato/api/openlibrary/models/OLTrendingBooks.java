package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLTrendingBooks {
    @SerializedName("works")
    private List<OLTrendingBook> works;

    public List<OLTrendingBook> getWorks() {
        return works;
    }
}

