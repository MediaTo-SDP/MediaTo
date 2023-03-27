package com.github.sdp.mediato.api.themoviedb;

import androidx.lifecycle.MutableLiveData;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.themoviedb.models.PagedResult;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.util.AdapterRetrofitCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class that implements TheMovieDB API to return movies
 */
public class TheMovieDB implements API<TMDBMovie> {
    private final TheMovieDBAPI api;
    private final String apikey;
    private final HashMap<String, List<TMDBMovie>> searchCache;

    private final HashMap<String, Integer> searchPage;
    private List<TMDBMovie> trendingCache;
    private int trendingPage;

    private MutableLiveData<TMDBMovie> livedata;

    /**
     * Default constructor
     * @param serverUrl domain name of the api (used to inject tests)
     * @param apikey the key provided by TheMovieDB to use their API
     */
    public TheMovieDB(String serverUrl, String apikey){
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
        this.api = retrofit.create(TheMovieDBAPI.class);
    }

    /**
     * Search a single instance of a movie on TheMovieDB
     * @param s the search term
     * @return the first search result not already displayed
     */
    @Override
    public CompletableFuture<TMDBMovie> searchItem(String s) {
        Preconditions.checkNullOrEmptyString(s, "Argument");
        return searchItems(s, 1).thenApply(array -> array.get(0));
    }

    /**
     * Search for multiple movie results on TheMovieDB. Never returns two times the same TheMovieDB
     * movie without a cache clear.
     * @param s the search term
     * @param count the number of requested movies (might be different of the size of the returned list)
     * @return a future that returns a list containing a maximum of @count movies
     *    It might be less since each function call import a maximum of 20 result from the TheMovieDB.
     */
    @Override
    public CompletableFuture<List<TMDBMovie>> searchItems(String s, int count) {
        Preconditions.checkNullOrEmptyString(s, "Argument");
        Preconditions.checkStrictlyPositive(count);
        boolean cacheExists = searchCache.containsKey(s) && searchCache.get(s) != null;
        List<TMDBMovie> oldCache = (cacheExists) ? searchCache.get(s): new ArrayList<>();

        // Do not request the server if we have some local data to proceed
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

        // Updates the cache when the request returns
        return future.thenApply(pagedResult -> {
            if (pagedResult.getPage() == pagedResult.getTotal_pages()) { searchPage.put(s, -1); }
            oldCache.addAll(pagedResult.getResults());
            int resultSize = Math.min(count, oldCache.size());
            List<TMDBMovie> results = new ArrayList<>(oldCache.subList(0, resultSize));
            searchCache.put(s, new ArrayList<>(oldCache.subList(resultSize, oldCache.size())));
            return results;
        });
    }

    /**
     * Load the list of trending movies during the past week. Never returns two times the same TheMovieDB
     * movie without a cache clear.
     * @param count the number of requested movies (might be different of the size of the returned list)
     * @return a future that returns a list containing a maximum of @count movies
     *    It might be less since each function call import a maximum of 20 result from the TheMovieDB.
     */
    @Override
    public CompletableFuture<List<TMDBMovie>> trending(int count){
        Preconditions.checkStrictlyPositive(count);

        // Do not request the server if we have some local data to proceed
        if(trendingCache.size() >= count){
            List<TMDBMovie> result = new ArrayList<>(trendingCache.subList(0, count));
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

        // Update the local cache when the request returns
        return completableFuture.thenApply(pagedResult -> {
            if (pagedResult.getTotal_pages() == pagedResult.getPage()){ trendingPage = -1; }
            trendingCache.addAll(pagedResult.getResults());
            int resultSize = Math.min(count, trendingCache.size());
            List<TMDBMovie> results = new ArrayList<>(trendingCache.subList(0, resultSize));
            trendingCache = new ArrayList<>(trendingCache.subList(resultSize, trendingCache.size()));
            return results;
        });
    }

    /**
     * Get a single movie from it's TMDB id
     * @param id the TMDB id of the movie
     * @return a completable holding the movie data
     */
    @Override
    public CompletableFuture<TMDBMovie> get(int id) {
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
}
