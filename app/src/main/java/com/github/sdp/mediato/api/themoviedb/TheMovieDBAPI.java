package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.themoviedb.models.PagedResult;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieDBAPI {
    @GET("search/movie")
    Call<PagedResult<TMDBMovie>> searchItem(@Query("api_key") String apiKey,
                                            @Query("query") String query,
                                            @Query("language") String language);
    @GET("trending/movie/week")
    Call<PagedResult<TMDBMovie>> trendingFilms(@Query("api_key") String apiKey);
}
