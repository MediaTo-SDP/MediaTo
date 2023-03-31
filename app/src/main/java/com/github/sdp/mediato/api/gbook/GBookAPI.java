package com.github.sdp.mediato.api.gbook;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.gbook.models.GBookSearchResult;
import com.github.sdp.mediato.api.gbook.models.GoogleBook;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.utility.adapters.AdapterRetrofitCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GBookAPI implements API<GoogleBook> {
    private static final int RES_PER_REQUEST = 40;
    private final GbookAPIInterface api;
    private final Map<String, List<GoogleBook>> cache;
    private final Map<String, Integer> indices;


    public GBookAPI(String serverUrl) {
        Preconditions.checkNullOrEmptyString(serverUrl, "server Url");
        this.cache = new HashMap<>();
        this.indices = new HashMap<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.api = retrofit.create(GbookAPIInterface.class);
    }

    @Override
    public CompletableFuture<GoogleBook> searchItem(String s) {
        Preconditions.checkNullOrEmptyString(s, "search term");
        return searchItems(s, 1).thenApply((list) -> list.get(0));
    }

    @Override
    public CompletableFuture<List<GoogleBook>> searchItems(String s, int count) {
        Preconditions.checkNullOrEmptyString(s, "search term");
        Preconditions.checkStrictlyPositive(count);
        // cache.getOrDefault could have been null
        List<GoogleBook> _termCache = cache.getOrDefault(s, new ArrayList<>());
        List<GoogleBook> termCache = (_termCache == null) ? new ArrayList<>() : _termCache;


        // If there is remaining data, do not update
        if (termCache.size() >= count) {
            List<GoogleBook> res = new ArrayList<>(termCache.subList(0, count));
            cache.put(s, termCache.subList(count, termCache.size()));
            return CompletableFuture.completedFuture(res);
        }

        Integer index = indices.getOrDefault(s, 0);
        index = (index != null) ? index : 0;
        // No more data to download
        if ( index < 0 ){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        CompletableFuture<GBookSearchResult> future = new CompletableFuture<>();
        api.search(s, "en", index, RES_PER_REQUEST)
                .enqueue(new AdapterRetrofitCallback<>(future));
        indices.put(s, index + RES_PER_REQUEST);

        return future.thenApply(searchRes -> {
            if (searchRes.getItems()!= null && searchRes.getItems().size() < 1){
                indices.put(s, -1);
                termCache.addAll(searchRes.getItems());
            }
            int resCount = Math.min(count, termCache.size());
            cache.put(s, new ArrayList<>(termCache.subList(resCount, termCache.size())));
            return new ArrayList<>(termCache.subList(0, resCount));
        });

    }

    @Override
    public CompletableFuture<GoogleBook> get(String id) {
        CompletableFuture<GoogleBook> future = new CompletableFuture<>();
        api.get(id).enqueue(new AdapterRetrofitCallback<>(future));
        return future;
    }

    @Override
    public void clearCache() {
        cache.clear();
        indices.clear();
    }
}
