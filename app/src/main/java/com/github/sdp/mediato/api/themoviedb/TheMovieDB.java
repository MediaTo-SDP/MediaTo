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
    private ArrayList<TMDBMovie> trendingCache;

    public TheMovieDB(String apikey){
        this.apikey = apikey;
        searchCache = new HashMap<>();
        trendingCache = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(TheMovieDBAPI.class);
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

        CompletableFuture<PagedResult<TMDBMovie>> future = new CompletableFuture<>();
        api.searchItem(apikey, s, "en-US")
                .enqueue(new AdapterRetrofitCallback<>(future));

        return future.thenApply(pagedResult -> {
            List<TMDBMovie> results = pagedResult.getResults();
            ArrayList<TMDBMovie> newCache = new ArrayList<>(results.subList(count, results.size()));
            newCache.addAll(oldCache);
            searchCache.put(s, newCache);
            return new ArrayList<>(results.subList(0, count));
        });
    }

    @Override
    public CompletableFuture<ArrayList<TMDBMovie>> trending(int count){
        if(trendingCache.size() >= count){
            ArrayList<TMDBMovie> result = new ArrayList<>(trendingCache.subList(0, count));
            trendingCache = new ArrayList<>(trendingCache.subList(count, trendingCache.size()));
            return CompletableFuture.completedFuture(result);
        }
        CompletableFuture<PagedResult<TMDBMovie>> completableFuture = new CompletableFuture<>();
        api.trendingFilms(apikey)
                .enqueue(new AdapterRetrofitCallback<>(completableFuture));
        return completableFuture.thenApply(pagedResult -> {
            List<TMDBMovie> result = pagedResult.getResults();
            trendingCache.addAll(result.subList(count, result.size()));
            return new ArrayList<>(result.subList(0, count));
        });
    }
}
