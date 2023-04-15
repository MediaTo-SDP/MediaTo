package com.github.sdp.mediato.api.gbook.models;

import java.util.List;

public final class GBookSearchResult {
    private List<GoogleBook> items;
    private int totalItems;

    private GBookSearchResult() {
        // Not possible to create a custom GBookSearchResult
    }

    public List<GoogleBook> getItems() {
        return items;
    }

}
