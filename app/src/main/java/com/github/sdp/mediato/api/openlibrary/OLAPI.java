package com.github.sdp.mediato.api.openlibrary;

import com.github.sdp.mediato.api.API;
import com.github.sdp.mediato.api.openlibrary.models.OLTrendingBooks;
import com.github.sdp.mediato.api.themoviedb.TheMovieDBAPIInterface;
import com.github.sdp.mediato.api.themoviedb.models.PagedResult;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;
import com.github.sdp.mediato.errorCheck.Preconditions;
import com.github.sdp.mediato.utility.adapters.AdapterRetrofitCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OLAPI implements API {

    private final OLAPIInterface api;

    /**
     * Default constructor
     *
     * @param serverUrl domain name of the api (used to inject tests)
     */
    public OLAPI(String serverUrl) {
        Preconditions.checkNullOrEmptyString(serverUrl, "serverUrl");

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY); // Use Level.BASIC for just the request method and URL.

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        this.api = retrofit.create(OLAPIInterface.class);
    }
    @Override
    public CompletableFuture searchItem(String s) {
        return null;
    }

    @Override
    public CompletableFuture<List> searchItems(String s, int count) {
        return null;
    }

    public CompletableFuture<OLTrendingBooks> trending(int count) {
        CompletableFuture<OLTrendingBooks> completableFuture = new CompletableFuture<>();
        api.getTrendingBooks(1)
                .enqueue(new AdapterRetrofitCallback<>(completableFuture));

        return completableFuture.thenApply(olTrendingBooks -> {
            String query = StringBuilder
            return olTrendingBooks.getWorks().stream().map(x -> x.getKey()).collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture get(String id) {
        return null;
    }
}
