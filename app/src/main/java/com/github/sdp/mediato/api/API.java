package com.github.sdp.mediato.api;

import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface API<T> {
    CompletableFuture<T> searchItem(String s);

    CompletableFuture<List<T>> searchItems(String s, int count);

    CompletableFuture<List<T>> trending(int count);

    CompletableFuture<T> get(int id);

    default void clearCache(){ }
}
