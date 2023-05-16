package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.themoviedb.models.PagedResult;
import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface used by Retrofit to request TheMovieDBAPI api
 */
public interface TheMovieDBAPIInterface {
    @GET("3/search/movie")
    CompletableFuture<PagedResult> getSearchMovie(@Query("api_key") String apiKey,
                                                         @Query("include_adult") boolean include_adult,
                                                         @Query("query") String query,
                                                         @Query("language") String language,
                                                         @Query("page") int page);

    @GET("3/discover/movie")
    CompletableFuture<PagedResult> getTrendingMovies(@Query("api_key") String apiKey,
                                               @Query("include_adult") boolean include_adult,
                                               @Query("language") String language,
                                               @Query("sort_by") String sortBy,
                                               @Query("primary_release_year") Integer year,
                                               @Query("with_genres") Integer genreId,
                                               @Query("page") int page);
}
