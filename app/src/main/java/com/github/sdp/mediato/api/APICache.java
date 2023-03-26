package com.github.sdp.mediato.api;

import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class APICache implements API<T>{

    private final
    @Override
    public CompletableFuture<T> searchItem(String s) {
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
