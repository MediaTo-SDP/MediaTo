package com.github.sdp.mediato.api.themoviedb.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class used by the retrofit library to store the movies returned by TheMovieDBRetrofitInterface api.
 */
public final class TMDBMovie {

    private static final String POSTER_URL = "https://image.tmdb.org/t/p/original";
    private static final String ICON_URL = "https://image.tmdb.org/t/p/w154";

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    @SerializedName("id")
    private int id;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("overview")
    private String overview;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("title")
    private String title;

    @SerializedName("video")
    private boolean video;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    public String getRelease_date() {
        return releaseDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon_path() {
        return ICON_URL + posterPath;
    }

    public String getPoster_path() {
        return POSTER_URL + posterPath;
    }

    public String getOverview() {
        return overview;
    }
}
