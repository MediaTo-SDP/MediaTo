package com.github.sdp.mediato.api.themoviedb.models;

import java.util.List;

/**
 * Class wrapper for the API returns of TheMovieDB
 * @param <T> The type of the result
 */
public final class PagedResult<T> {
    private int page;
    private List<T> results;
    private int total_pages;
    private int total_results;

    public int getPage() {
        return page;
    }

    public List<T> getResults() {
        return results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }
}
