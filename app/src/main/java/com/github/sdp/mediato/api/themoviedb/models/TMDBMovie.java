package com.github.sdp.mediato.api.themoviedb.models;

import java.util.List;

/**
 * Class used by the retrofit library to store the movies returned by TheMovieDBAPI api.
 */
public final class TMDBMovie {
    private static final String POSTER_URL = "https://image.tmdb.org/t/p/original";
    private static final String ICON_URL = "https://image.tmdb.org/t/p/w154";

    private String poster_path;
    private boolean adult;
    private String overview;
    private String release_date;
    private List<Integer> genre_ids;
    private int id;
    private String original_title;
    private String original_language;
    private String title;
    private String backdrop_path;
    private double popularity;
    private int vote_count;
    private boolean video;
    private double voteAverage;

    public String getRelease_date() {
        return release_date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon_path() {
        return ICON_URL + poster_path;
    }

    public String getPoster_path() {
        return POSTER_URL + poster_path;
    }

    public String getOverview() {
        return overview;
    }
}
