package com.github.sdp.mediato.model.media;

import com.github.sdp.mediato.api.themoviedb.models.TMDBMovie;
import com.github.sdp.mediato.model.media.MediaType;

public class Movie extends Media{

    public Movie(String title, String summary, String imageUrl, int id) {
        super(MediaType.MOVIE, title, summary, imageUrl, id);
    }
    public Movie(TMDBMovie tmdbMovie){
        this(tmdbMovie.getTitle(), tmdbMovie.getOverview(),
                tmdbMovie.getPoster_path(), tmdbMovie.getId());
    }

    @Override
    public String toString() {
        return "Movie title: " + getTitle() + "\n" +
                "Summary: " + getSummary();
    }
}
