package com.github.sdp.mediato.api.openlibrary;

import com.github.sdp.mediato.api.openlibrary.models.OLBookDetails;
import com.github.sdp.mediato.api.openlibrary.models.OLTrendingBooks;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OLAPIInterface {
    @GET("/trending/yearly.json")
    CompletableFuture<OLTrendingBooks> getTrendingBooks(@Query("page") int page);

    @GET("/works/{key}.json")
    CompletableFuture<OLBookDetails> getBookDetails(@Path("key") String key);

}
