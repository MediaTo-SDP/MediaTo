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

    /**
     * Default api constructor
     * @param serverUrl the url of the server
     */
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

    /**
     * Get a the first returned (not already returned) book from a search with the api
     * @param s the search term(s)
     * @return a future containing the book
     */
    @Override
    public CompletableFuture<GoogleBook> searchItem(String s) {
        Preconditions.checkNullOrEmptyString(s, "search term");
        return searchItems(s, 1).thenApply((list) -> list.get(0));
    }

    /**
     * Searches for multiple Books on the API. Maximum 40 results are retrieved per call.
     * So you might use the returned list size to know how much data has been retrieved
     * (not larger than the requested amount).
     * @param s The search term(s)
     * @param count The amount of books requested
     * @return a future returning the list of books
     */
    @Override
    public CompletableFuture<List<GoogleBook>> searchItems(String s, int count) {
        Preconditions.checkNullOrEmptyString(s, "search term");
        Preconditions.checkStrictlyPositive(count);
        // cache.getOrDefault could have been null
        List<GoogleBook> termCache = getNonNullCache(s);


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

        return future.thenApply(searchRes -> updateDataCache(searchRes, count, s));
    }

    /**
     * Returns the information about a specific book
     * @param id the GoogleBook id of the book
     * @return the information of the book
     */
    @Override
    public CompletableFuture<GoogleBook> get(String id) {
        CompletableFuture<GoogleBook> future = new CompletableFuture<>();
        api.get(id).enqueue(new AdapterRetrofitCallback<>(future));
        return future;
    }

    /**
     * Clears the cache of already returned data. So that we can return the data that has
     * already been returned.
     */
    @Override
    public void clearCache() {
        cache.clear();
        indices.clear();
    }

    private List<GoogleBook> getNonNullCache(String s){
        List<GoogleBook> sCache = this.cache.getOrDefault(s, new ArrayList<>());
        return (sCache != null) ? sCache : new ArrayList<>();
    }

    private List<GoogleBook> updateDataCache(GBookSearchResult searchRes , int requestedAmount, String s){
        List<GoogleBook> sCache = getNonNullCache(s);
        if (searchRes.getItems() == null || searchRes.getItems().size() < 1){
            indices.put(s, -1);
        } else {
            sCache.addAll(searchRes.getItems());
        }
        int resCount = Math.min(requestedAmount, sCache.size());
        this.cache.put(s, new ArrayList<>(sCache.subList(resCount, sCache.size())));
        return new ArrayList<>(sCache.subList(0, resCount));
    }


}
