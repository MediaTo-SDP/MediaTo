package com.github.sdp.mediato.api.openlibrary;

import com.github.sdp.mediato.api.openlibrary.models.OLBookDetails;
import com.github.sdp.mediato.api.openlibrary.models.OLSearchBookResponse;
import com.github.sdp.mediato.api.openlibrary.models.OLTrendingBooks;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OLAPIInterface {
    @GET("/search.json")
    CompletableFuture<OLSearchBookResponse> getSearchBook(
            @Query("title") String title,
            @Query("page") int page
    );

    @GET("/trending/yearly.json")
    CompletableFuture<OLTrendingBooks> getTrendingBooks(@Query("page") int page);

    @GET("/works/{key}.json")
    CompletableFuture<OLBookDetails> getBookDetails(@Path("key") String key);

}
