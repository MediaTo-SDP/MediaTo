package com.github.sdp.mediato.api.gbook;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.gbook.models.GoogleBook;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GBookAPI implements API<GoogleBook> {
    private final GbookAPIInterface api;
    private fina


    @Override
    public CompletableFuture<GoogleBook> searchItem(String s) {
        return null;
    }

    @Override
    public CompletableFuture<List<TMDBMovie>> searchItems(String s, int count) {
        return null;
    }

    @Override
    public CompletableFuture<List<TMDBMovie>> trending(int count) {
        return null;
    }

    @Override
    public void clearCache() {

    }
}
