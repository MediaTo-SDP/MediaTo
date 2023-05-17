package com.github.sdp.mediato.api.themoviedb.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class wrapper for the API returns of TheMovieDBRetrofitInterface
 *
 */
public final class PagedResult {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<TMDBMovie> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public List<TMDBMovie> getResults() {
        return results;
    }
}
