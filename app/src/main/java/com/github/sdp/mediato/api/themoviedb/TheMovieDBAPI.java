package com.github.sdp.mediato.api.themoviedb;

import com.github.sdp.mediato.api.themoviedb.models.TMDBFilm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieDBAPI {
    @GET("/search/movie")
    Call<TMDBFilm> searchItem(@Query("api_key") String apiKey,
                              @Query("query") String query,
                              @Query("language") String language);
}
