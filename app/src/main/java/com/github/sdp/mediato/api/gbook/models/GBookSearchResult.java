package com.github.sdp.mediato.api.gbook.models;

import java.util.List;

public class GBookSearchResult {
    public List<GoogleBook> getItems() {
        return items;
    }

    private GBookSearchResult() {
        // Not possible to create a custom GBookSearchResult
    }

    public int getTotalItems() {
        return totalItems;
    }

    private List<GoogleBook> items;
    private int totalItems;

}
