package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLTrendingBook {
    @SerializedName("title")
    private String title;
    @SerializedName("first_publish_year")
    private int firstPublishYear;
    @SerializedName("cover_i")
    private int coverI;
    @SerializedName("availability")
    private Availability availability;

    public String getKey() {
        return availability.getOpenlibraryWork();
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

    public static class Availability {
        @SerializedName("openlibrary_work")
        private String openlibraryWork;

        public String getOpenlibraryWork() {
            return openlibraryWork;
        }
    }
}
