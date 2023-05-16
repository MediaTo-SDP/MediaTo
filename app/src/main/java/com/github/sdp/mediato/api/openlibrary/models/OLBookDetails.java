package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLBookDetails {
    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }
}