package com.github.sdp.mediato.api.gbook.models;

import java.util.List;

public class SearchResult {
    public List<GoogleBook> getItems() {
        return items;
    }

    private SearchResult(){
        // Not possible to create a custom SearchResult
    }

    public int getTotalItems() {
        return totalItems;
    }

    private List<GoogleBook> items;
    private int totalItems;

}
