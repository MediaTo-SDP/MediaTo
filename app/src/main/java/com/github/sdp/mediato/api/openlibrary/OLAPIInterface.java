package com.github.sdp.mediato.api.openlibrary;

import com.github.sdp.mediato.api.openlibrary.models.OLTrendingBooks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OLAPIInterface {
    @GET("/trending/yearly.json")
    Call<OLTrendingBooks> getTrendingBooks(@Query("page") int page);
}
