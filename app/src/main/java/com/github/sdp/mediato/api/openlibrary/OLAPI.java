package com.github.sdp.mediato.api.openlibrary;

import com.github.sdp.mediato.api.API;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OLAPI implements API {
    @Override
    public CompletableFuture searchItem(String s) {
        return null;
    }

    @Override
    public CompletableFuture<List> searchItems(String s, int count) {
        return null;
    }

    @Override
    public CompletableFuture get(String id) {
        return null;
    }
}
