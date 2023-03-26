package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.themoviedb.models.PagedResult;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface used by Retrofit to request TheMovieDBAPI api
 */
public interface TheMovieDBAPIInterface {
    @GET("search/movie")
    Call<PagedResult<TMDBMovie>> searchItem(@Query("api_key") String apiKey,
                                            @Query("query") String query,
                                            @Query("language") String language,
                                            @Query("page") int page);
    @GET("trending/movie/week")
    Call<PagedResult<TMDBMovie>> trendingFilms(@Query("api_key") String apiKey,
                                               @Query("language") String language,
                                               @Query("page") int page);
}
