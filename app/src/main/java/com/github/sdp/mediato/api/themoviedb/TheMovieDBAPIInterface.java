package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.themoviedb.models.PagedResult;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface used by Retrofit to request TheMovieDBAPI api
 */
public interface TheMovieDBAPIInterface {
    @GET("3/search/movie?include_adult=false&language=en-Us")
    CompletableFuture<PagedResult> getSearchMovie(@Query("api_key") String apiKey,
                                                         @Query("query") String query,
                                                         @Query("page") int page);

    @GET("3/discover/movie?include_adult=false&language=en-Us&sort_by=popularity.desc")
    CompletableFuture<PagedResult> getTrendingMovies(@Query("api_key") String apiKey,
                                               @Query("primary_release_year") Integer year,
                                               @Query("with_genres") Integer genreId,
                                               @Query("page") int page);
}
