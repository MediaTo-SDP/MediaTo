package com.github.sdp.mediato.api;

import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface API<T> {
    CompletableFuture<T> searchItem(String s);

    CompletableFuture<List<TMDBMovie>> searchItems(String s, int count);

    CompletableFuture<List<TMDBMovie>> trending(int count);

    void clearCache();
}
