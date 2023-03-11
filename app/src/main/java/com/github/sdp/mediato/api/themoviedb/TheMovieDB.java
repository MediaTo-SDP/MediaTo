package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.themoviedb.models.PagedResult;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;
import com.github.sdp.mediato.util.AdapterRetrofitCallback;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheMovieDB implements API<TMDBMovie> {
    private final TheMovieDBAPI api;
    private final String apikey;
    private final HashMap<String, ArrayList<TMDBMovie>> searchCache;

    private final HashMap<String, Integer> searchPage;
    private ArrayList<TMDBMovie> trendingCache;
    private int trendingPage;

    public TheMovieDB(String serverUrl, String apikey){
        this.trendingCache = new ArrayList<>();
        this.trendingPage = 0;
        this.searchCache = new HashMap<>();
        this.searchPage = new HashMap<>();
        this.apikey = apikey;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.api = retrofit.create(TheMovieDBAPI.class);
    }

    @Override
    public CompletableFuture<TMDBMovie> searchItem(String s) {
        return searchItems(s, 1).thenApply(array -> array.get(0));
    }

    @Override
    public CompletableFuture<ArrayList<TMDBMovie>> searchItems(String s, int count) {
        boolean cacheExists = searchCache.containsKey(s) && searchCache.get(s) != null;
        ArrayList<TMDBMovie> oldCache = (cacheExists) ? searchCache.get(s): new ArrayList<>();

        if (cacheExists && oldCache.size() >= count){
            ArrayList<TMDBMovie> result = new ArrayList<>(oldCache.subList(0, count));
            searchCache.put(s, new ArrayList<>(oldCache.subList(count, oldCache.size())));
            return CompletableFuture.completedFuture(result);
        }

        // No more valid page before the cache is cleared
        if (searchPage.getOrDefault(s, 0) < 0) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        CompletableFuture<PagedResult<TMDBMovie>> future = new CompletableFuture<>();
        int currentPage = searchPage.getOrDefault(s, 0);
        api.searchItem(apikey, s, "en-US", ++currentPage)
                .enqueue(new AdapterRetrofitCallback<>(future));
        searchPage.put(s, currentPage);

        return future.thenApply(pagedResult -> {
            if (pagedResult.getPage() == pagedResult.getTotal_pages()) { searchPage.put(s, -1); }
            oldCache.addAll(pagedResult.getResults());
            int resultSize = Math.min(count, oldCache.size());
            ArrayList<TMDBMovie> results = new ArrayList<>(oldCache.subList(0, resultSize));
            searchCache.put(s, new ArrayList<>(oldCache.subList(resultSize, oldCache.size())));
            return results;
        });
    }

    @Override
    public CompletableFuture<ArrayList<TMDBMovie>> trending(int count){
        if(trendingCache.size() >= count){
            ArrayList<TMDBMovie> result = new ArrayList<>(trendingCache.subList(0, count));
            trendingCache = new ArrayList<>(trendingCache.subList(count, trendingCache.size()));
            return CompletableFuture.completedFuture(result);
        }
        // No more valid data before cache clear
        if (trendingPage < 0){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        CompletableFuture<PagedResult<TMDBMovie>> completableFuture = new CompletableFuture<>();
        api.trendingFilms(apikey, "en-US", ++trendingPage)
                .enqueue(new AdapterRetrofitCallback<>(completableFuture));

        return completableFuture.thenApply(pagedResult -> {
            if (pagedResult.getTotal_pages() == pagedResult.getPage()){ trendingPage = -1; }
            trendingCache.addAll(pagedResult.getResults());
            int resultSize = Math.min(count, trendingCache.size());
            ArrayList<TMDBMovie> results = new ArrayList<>(trendingCache.subList(0, resultSize));
            trendingCache = new ArrayList<>(trendingCache.subList(resultSize, trendingCache.size()));
            return results;
        });
    }

    @Override
    public void clearCache() {
        trendingPage = 0;
        trendingCache.clear();
        searchCache.clear();
        searchPage.clear();
    }
}
