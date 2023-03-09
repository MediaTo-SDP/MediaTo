package com.github.sdp.mediato.api.themoviedb.models;

import java.util.List;

public class PagedResult<T>{
    private int page;
    private List<T> results;
    private int total_pages;
    private int total_results;
}
