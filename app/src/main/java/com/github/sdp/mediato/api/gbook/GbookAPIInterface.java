package com.github.sdp.mediato.api.gbook;

import com.github.sdp.mediato.api.gbook.models.GBookSearchResult;
import com.github.sdp.mediato.api.gbook.models.GoogleBook;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 *
 */
public interface GbookAPIInterface {
    @GET("books/v1/volumes")
    Call<GBookSearchResult> search(@Query("q") String query,
                                   @Query("langRestrict") String language,
                                   @Query("startIndex") int startIndex,
                                   @Query("maxResults") int numberOfResults);

    @GET("books/v1/volumes/{id}")
    Call<GoogleBook> get(@Path("id") String id);

}
