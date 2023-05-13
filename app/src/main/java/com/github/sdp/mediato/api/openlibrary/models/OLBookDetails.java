package com.github.sdp.mediato.api.openlibrary.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OLBookDetails {

    @SerializedName("description")
    private Description description;

    @SerializedName("subjects")
    private List<String> subjects;

    public Description getDescription() {
        return description;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public static class Description {
        @SerializedName("type")
        private String type;

        @SerializedName("value")
        private String value;

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }
}