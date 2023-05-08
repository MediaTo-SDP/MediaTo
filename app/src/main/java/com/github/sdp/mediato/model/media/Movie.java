package com.github.sdp.mediato.model.media;

import androidx.annotation.NonNull;

import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;

public class Movie extends Media{

    /**
     * Custom constructor that allow to adapt / copy a Movie
     * @param media the media to adapt / copy from
     * @param <T> the type of this media
     */
    public <T extends Media> Movie(T media){
        super(MediaType.MOVIE, media);
    }

    /**
     * Default constructor that takes all values one by one
     * @param title the title of the movie
     * @param summary a summary of the movie
     * @param posterUrl an URL of a large size reference image for the movie
     * @param iconUrl an URL of a small size reference image for the movie
     * @param id the unique identifier at our data provider
     */
    public Movie(String title, String summary, String posterUrl, String iconUrl, String id) {
        super(MediaType.MOVIE, title, summary, posterUrl, iconUrl, id);
    }

    @Deprecated
    public Movie(String title, String summary, String imageUrl, int id) {
        super(MediaType.MOVIE, title, summary, imageUrl, id);
    }

    /**
     * Custom constructor that allows an easy conversion from the TheMovieDatabase API
     * @param tmdbMovie The movie provided by the API
     */
    public Movie(TMDBMovie tmdbMovie){
        this(tmdbMovie.getTitle(), tmdbMovie.getOverview(), tmdbMovie.getIcon_path(),
                tmdbMovie.getPoster_path(), Integer.toString(tmdbMovie.getId()));
    }

    /**
     * Get a descriptive string for the current movie object
     * @return the description of this movie object
     */
    @NonNull
    @Override
    public String toString() {
        return "Movie title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
