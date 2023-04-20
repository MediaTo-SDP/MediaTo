package com.github.sdp.mediato.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface API<T> {
    CompletableFuture<T> searchItem(String s);

    CompletableFuture<List<T>> searchItems(String s, int count);

    // Removed since the book API does not provide a trending feature. Might be added again later
    // CompletableFuture<List<T>> trending(int count);

    CompletableFuture<T> get(String id);

    default void clearCache(){ }
}
