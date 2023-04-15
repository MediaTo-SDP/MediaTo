package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.themoviedb.models.PagedResult;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.utility.adapters.AdapterRetrofitCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class that implements TheMovieDBAPI API to return movies
 */
public class TheMovieDBAPI implements API<TMDBMovie> {
    private final TheMovieDBAPIInterface api;
    private final String apikey;
    private final HashMap<String, List<TMDBMovie>> searchCache;

    private final HashMap<String, Integer> searchPage;
    private List<TMDBMovie> trendingCache;
    private int trendingPage;

    /**
     * Default constructor
     *
     * @param serverUrl domain name of the api (used to inject tests)
     * @param apikey    the key provided by TheMovieDBAPI to use their API
     */
    public TheMovieDBAPI(String serverUrl, String apikey) {
        Preconditions.checkNullOrEmptyString(serverUrl, "serverUrl");
        Preconditions.checkNullOrEmptyString(apikey, "apikey");
        this.trendingCache = new ArrayList<>();
        this.trendingPage = 0;
        this.searchCache = new HashMap<>();
        this.searchPage = new HashMap<>();
        this.apikey = apikey;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.api = retrofit.create(TheMovieDBAPIInterface.class);
    }

    /**
     * Search a single instance of a movie on TheMovieDBAPI
     *
     * @param s the search term
     * @return the first search result not already displayed
     */
    @Override
    public CompletableFuture<TMDBMovie> searchItem(String s) {
        Preconditions.checkNullOrEmptyString(s, "Argument");
        return searchItems(s, 1).thenApply(array -> array.get(0));
    }

    /**
     * Search for multiple movie results on TheMovieDBAPI. Never returns two times the same TheMovieDBAPI
     * movie without a cache clear.
     *
     * @param s     the search term
     * @param count the number of requested movies (might be different of the size of the returned list)
     * @return a future that returns a list containing a maximum of @count movies
     * It might be less since each function call import a maximum of 20 result from the TheMovieDBAPI.
     */
    @Override
    public CompletableFuture<List<TMDBMovie>> searchItems(String s, int count) {
        Preconditions.checkNullOrEmptyString(s, "Argument");
        Preconditions.checkStrictlyPositive(count);
        // oldCache should not be reassigned
        final List<TMDBMovie> oldCache = getNonNullCache(s);

        // Do not request the server if we have some local data to proceed
        if (oldCache.size() >= count) {
            ArrayList<TMDBMovie> result = new ArrayList<>(oldCache.subList(0, count));
            searchCache.put(s, new ArrayList<>(oldCache.subList(count, oldCache.size())));
            return CompletableFuture.completedFuture(result);
        }
        Integer index = searchPage.getOrDefault(s, 0);
        index = (index == null) ? 0 : index;
        // No more valid page before the cache is cleared
        if (index < 0) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        CompletableFuture<PagedResult<TMDBMovie>> future = new CompletableFuture<>();
        api.searchItem(apikey, s, "en-US", ++index)
                .enqueue(new AdapterRetrofitCallback<>(future));
        searchPage.put(s, index);

        // Updates the cache when the request returns
        return future.thenApply(pagedResult -> updateSearchCache(pagedResult, count, s));
    }

    /**
     * Load the list of trending movies during the past week. Never returns two times the same TheMovieDBAPI
     * movie without a cache clear.
     *
     * @param count the number of requested movies (might be different of the size of the returned list)
     * @return a future that returns a list containing a maximum of @count movies
     * It might be less since each function call import a maximum of 20 result from the TheMovieDBAPI.
     */
    public CompletableFuture<List<TMDBMovie>> trending(int count) {
        Preconditions.checkStrictlyPositive(count);

        // Do not request the server if we have some local data to proceed
        if (trendingCache.size() >= count) {
            List<TMDBMovie> result = new ArrayList<>(trendingCache.subList(0, count));
            trendingCache = new ArrayList<>(trendingCache.subList(count, trendingCache.size()));
            return CompletableFuture.completedFuture(result);
        }

        // No more valid data before cache clear
        if (trendingPage < 0) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        CompletableFuture<PagedResult<TMDBMovie>> completableFuture = new CompletableFuture<>();
        api.trendingFilms(apikey, "en-US", ++trendingPage)
                .enqueue(new AdapterRetrofitCallback<>(completableFuture));

        // Update the local cache when the request returns
        return completableFuture.thenApply(pagedResult -> {
            if (pagedResult.getTotal_pages() == pagedResult.getPage()) {
                trendingPage = -1;
            }
            trendingCache.addAll(pagedResult.getResults());
            int resultSize = Math.min(count, trendingCache.size());
            List<TMDBMovie> results = new ArrayList<>(trendingCache.subList(0, resultSize));
            trendingCache = new ArrayList<>(trendingCache.subList(resultSize, trendingCache.size()));
            return results;
        });
    }

    /**
     * Get a single movie from it's TMDB id
     *
     * @param id the TMDB id of the movie
     * @return a completable holding the movie data
     */
    @Override
    public CompletableFuture<TMDBMovie> get(String id) {
        CompletableFuture<TMDBMovie> future = new CompletableFuture<>();
        api.get(id, apikey, "en-US").enqueue(new AdapterRetrofitCallback<>(future));
        return future;
    }

    /**
     * Clears the local cache. Should be called before each search loop.
     */
    @Override
    public void clearCache() {
        trendingPage = 0;
        trendingCache.clear();
        searchCache.clear();
        searchPage.clear();
    }

    private List<TMDBMovie> getNonNullCache(String s){
        List<TMDBMovie> _oldCache = searchCache.getOrDefault(s, new ArrayList<>());
        return (_oldCache == null) ? new ArrayList<>() : _oldCache;
    }

    private List<TMDBMovie> updateSearchCache(PagedResult<TMDBMovie> pagedResult, int requestedSize, String s){
        final List<TMDBMovie> oldCache = getNonNullCache(s);
        if (pagedResult.getPage() == pagedResult.getTotal_pages()) {
            searchPage.put(s, -1);
        }
        oldCache.addAll(pagedResult.getResults());
        int resultSize = Math.min(requestedSize, oldCache.size());
        searchCache.put(s, new ArrayList<>(oldCache.subList(resultSize, oldCache.size())));
        return new ArrayList<>(oldCache.subList(0, resultSize));
    }
}
