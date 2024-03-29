package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLSearchBookResponse {
    @SerializedName("docs")
    private List<OLBook> books;
    @SerializedName("numFound")
    private int numFound;
    @SerializedName("start")
    private int start;

    public List<OLBook> getBooks() {
        return books;
    }
}
