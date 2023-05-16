package com.github.sdp.mediato.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface API<T> {
    CompletableFuture<List<T>> searchItems(String s, int page);

    CompletableFuture<List<T>> trending(int page);
}
