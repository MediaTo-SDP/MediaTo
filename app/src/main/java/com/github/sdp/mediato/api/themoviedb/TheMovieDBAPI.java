package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.Movie;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class that implements TheMovieDBAPI API to return movies
 */
public class TheMovieDBAPI implements API<Media> {
    private final TheMovieDBAPIInterface api;
    private final String apikey;
    /**
     * Default constructor
     *
     * @param serverUrl domain name of the api (used to inject tests)
     * @param apikey    the key provided by TheMovieDBAPI to use their API
     */
    public TheMovieDBAPI(String serverUrl, String apikey) {
        Preconditions.checkNullOrEmptyString(serverUrl, "serverUrl");
        Preconditions.checkNullOrEmptyString(apikey, "apikey");
        this.apikey = apikey;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(TheMovieDBAPIInterface.class);
    }


    @Override
    public CompletableFuture<List<Media>> searchItems(String title, int page) {
        return api.getSearchMovie(apikey,false, title.replaceAll(" ","+"),"en-US", page)
                .thenApply(tmdbMoviePagedResult -> tmdbMoviePagedResult.getResults().stream()
                            .map(Movie::new)
                            .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Media>> trending(int page) {
        return api.getTrendingMovies(apikey, false,"en-US", "popularity.desc", null, null, page)
                .thenApply(tmdbMoviePagedResult -> tmdbMoviePagedResult.getResults().stream()
                        .map(Movie::new)
                        .collect(Collectors.toList()));
    }
}
